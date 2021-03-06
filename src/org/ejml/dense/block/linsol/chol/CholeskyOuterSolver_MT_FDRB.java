/*
 * Copyright (c) 2020, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml.dense.block.linsol.chol;

import javax.annotation.Generated;
import org.ejml.data.FGrowArray;
import org.ejml.data.FMatrixRBlock;
import org.ejml.data.FSubmatrixD1;
import org.ejml.dense.block.MatrixOps_FDRB;
import org.ejml.dense.block.TriangularSolver_FDRB;
import org.ejml.dense.block.decomposition.chol.CholeskyOuterForm_FDRB;
import org.ejml.dense.row.SpecializedOps_FDRM;
import org.ejml.interfaces.decomposition.CholeskyDecomposition_F32;
import org.ejml.interfaces.linsol.LinearSolverDense;
import org.jetbrains.annotations.Nullable;
import pabeles.concurrency.GrowArray;

import org.ejml.dense.block.decomposition.chol.CholeskyOuterForm_MT_FDRB;
import org.ejml.dense.block.TriangularSolver_MT_FDRB;
import org.ejml.concurrency.EjmlConcurrency;


/**
 * <p> Linear solver that uses a block cholesky decomposition.</p>
 *
 * <p>
 * Solver works by using the standard Cholesky solving strategy:<br>
 * A=L*L<sup>T</sup> <br>
 * A*x=b<br>
 * L*L<sup>T</sup>*x = b <br>
 * L*y = b<br>
 * L<sup>T</sup>*x = y<br>
 * x = L<sup>-T</sup>y
 * </p>
 *
 * <p>
 * It is also possible to use the upper triangular cholesky decomposition.
 * </p>
 *
 * @author Peter Abeles
 */
@SuppressWarnings("NullAway.Init")
@Generated("org.ejml.dense.block.linsol.chol.CholeskyOuterSolver_FDRB")
public class CholeskyOuterSolver_MT_FDRB implements LinearSolverDense<FMatrixRBlock> {

    // cholesky decomposition
    private final CholeskyOuterForm_MT_FDRB decomposer = new CholeskyOuterForm_MT_FDRB(true);

    // size of a block take from input matrix
    private int blockLength;

    // temporary data structure used in some calculation.
    private final GrowArray<FGrowArray> workspace = new GrowArray<>(FGrowArray::new);

    /**
     * Decomposes and overwrites the input matrix.
     *
     * @param A Semi-Positive Definite (SPD) system matrix. Modified. Reference saved.
     * @return If the matrix can be decomposed. Will always return false of not SPD.
     */
    @Override
    public boolean setA( FMatrixRBlock A ) {
        // Extract a lower triangular solution
        if (!decomposer.decompose(A))
            return false;

        blockLength = A.blockLength;

        return true;
    }

    @Override
    public /**/double quality() {
        return SpecializedOps_FDRM.qualityTriangular(decomposer.getT(null));
    }

    /**
     * If X == null then the solution is written into B. Otherwise the solution is copied
     * from B into X.
     */
    @Override
    public void solve( FMatrixRBlock B, @Nullable FMatrixRBlock X ) {
        if (B.blockLength != blockLength)
            throw new IllegalArgumentException("Unexpected blocklength in B.");

        FSubmatrixD1 L = new FSubmatrixD1(decomposer.getT(null));

        if (X == null) {
            X = B.create(L.col1, B.numCols);
        } else {
            X.reshape(L.col1, B.numCols, blockLength, false);
        }

        //  L * L^T*X = B

        // Solve for Y:  L*Y = B
        TriangularSolver_MT_FDRB.solve(blockLength, false, L, new FSubmatrixD1(B), false);

        // L^T * X = Y
        TriangularSolver_MT_FDRB.solve(blockLength, false, L, new FSubmatrixD1(B), true);

        if (X != null) {
            // copy the solution from B into X
            MatrixOps_FDRB.extractAligned(B, X);
        }
    }

    @Override
    public void invert( FMatrixRBlock A_inv ) {
        FMatrixRBlock T = decomposer.getT(null);
        if (A_inv.numRows != T.numRows || A_inv.numCols != T.numCols)
            throw new IllegalArgumentException("Unexpected number or rows and/or columns");

        // zero the upper triangular portion of A_inv
        MatrixOps_FDRB.zeroTriangle(true, A_inv);

        FSubmatrixD1 L = new FSubmatrixD1(T);
        FSubmatrixD1 B = new FSubmatrixD1(A_inv);

        // invert L from cholesky decomposition and write the solution into the lower
        // triangular portion of A_inv
        // B = inv(L)
        TriangularSolver_MT_FDRB.invert(blockLength, false, L, B, workspace);

        // B = L^-T * B
        // todo could speed up by taking advantage of B being lower triangular
        // todo take advantage of symmetry
        TriangularSolver_MT_FDRB.solveL(blockLength, L, B, true);
    }

    @Override
    public boolean modifiesA() {
        return decomposer.inputModified();
    }

    @Override
    public boolean modifiesB() {
        return true;
    }

    @Override
    public CholeskyDecomposition_F32<FMatrixRBlock> getDecomposition() {
        return decomposer;
    }
}
