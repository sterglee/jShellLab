/*
 * Copyright (c) 2009-2020, Peter Abeles. All Rights Reserved.
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

package org.ejml.dense.row.decomposition.bidiagonal;

import javax.annotation.Generated;
import org.ejml.data.FMatrixRMaj;
import org.ejml.dense.row.decomposition.qr.QrHelperFunctions_MT_FDRM;
import org.ejml.interfaces.decomposition.BidiagonalDecomposition_F32;

/**
 * <p>
 * Performs a {@link BidiagonalDecomposition_F32} using householder reflectors. This is efficient on wide or
 * square matrices.
 * </p>
 *
 * @author Peter Abeles
 */
@Generated("org.ejml.dense.row.decomposition.bidiagonal.BidiagonalDecompositionRow_MT_DDRM")
public class BidiagonalDecompositionRow_MT_FDRM
        extends BidiagonalDecompositionRow_FDRM {
    @Override
    protected void rank1UpdateMultL( FMatrixRMaj A, float gamma, int colA0, int w0, int w1 ) {
        QrHelperFunctions_MT_FDRM.rank1UpdateMultL(A, u, gamma, colA0, w0, w1);
    }

    @Override
    protected void rank1UpdateMultR( FMatrixRMaj A, float gamma, int colA0, int w0, int w1 ) {
        QrHelperFunctions_MT_FDRM.rank1UpdateMultR(A, u, gamma, colA0, w0, w1, this.b);
    }
}
