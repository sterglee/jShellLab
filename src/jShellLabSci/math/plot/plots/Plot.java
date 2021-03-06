package jShellLabSci.math.plot.plots;

import java.awt.Color;
import java.util.LinkedList;

import jShellLabSci.math.plot.canvas.PlotCanvas;
import jShellLabSci.math.plot.plotObjects.Editable;
import jShellLabSci.math.plot.plotObjects.Noteable;
import jShellLabSci.math.plot.plotObjects.Plotable;
import jShellLabSci.math.plot.render.AbstractDrawer;
import jShellLabSci.math.plot.utils.PArray;

public abstract class Plot implements Plotable, Noteable, Editable {
    public String name;
    public Color color;
    public boolean visible = true;
    public LinkedList<LayerPlot> layers;
    public boolean noted = false;
    public int note_precision = 5;

    public Plot(String n, Color c) {
        name = n;
        color = c;
        layers = new LinkedList<LayerPlot>();
    }

    public void addLayer(LayerPlot q) {
        layers.add(q);
    }

    public void addQuantile(QuantileLayerPlot q) {
        layers.add(q);
    }

    public void addQuantile(int a, double r, double[] q, boolean symetric) {
        layers.add(new QuantileLayerPlot(this, a, q, r, symetric));
    }

    public void addQuantile(int a, double r, double q, boolean symetric) {
        layers.add(new QuantileLayerPlot(this, a, q, r, symetric));
    }

    public void addQuantiles(int a, double[][] q) {
        layers.add(new DensityLayerPlot(this, a, q));
    }

    public void addQuantiles(int a, double[] q) {
        layers.add(new DensityLayerPlot(this, a, q));
    }

    public void addGaussQuantiles(int a, double[] s) {
        layers.add(new GaussianDensityLayerPlot(this, a, s));
    }

    public void addGaussQuantiles(int a, double s) {
        layers.add(new GaussianDensityLayerPlot(this, a, s));
    }

/*	public void addQuantiles(double[][][] q,boolean _symetric) {
		for (int i = 0; i < q[0].length; i++) {
			// addQuantile(i, PArray.getColumnCopy(q, i, 0),_symetric);
			// addQuantile(i,PArray.getColumnCopy(q, i, 1),_symetric);		}
	}

	public void addQuantiles(double[][] q,boolean _symetric) {
		for (int i = 0; i < q[0].length; i++) {
		//	addQuantile(i, PArray.getColumnCopy(q, i),_symetric);  // SOS-Sterg
		}
	}
*/
    public void addVector(double[][] v) {
        layers.add(new VectorLayerPlot(this, v));
}

    public abstract void setData(double[][] d);

    public abstract double[][] getData();

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

	/*
	 * public String getType() { return type; }
	 */

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public abstract double[] isSelected(int[] screenCoordTest, AbstractDrawer draw);

    public void note(AbstractDrawer draw) {
        plot(draw, PlotCanvas.NOTE_COLOR);
        plotLayerPlots(draw, PlotCanvas.NOTE_COLOR);
    }

    public abstract void plot(AbstractDrawer draw, Color c);

    public void plot(AbstractDrawer draw) {
        if (layers.size() > 0)
            plotLayerPlots(draw, color);
              else
            plot(draw, color);
    }

    public void plotLayerPlots(AbstractDrawer draw, Color c) {
        for (int i = 0; i < layers.size(); i++)
            layers.get(i).plot(draw, c);
    }

    public void edit(Object src) {
        ((PlotCanvas) src).displayDatasFrame(((PlotCanvas) src).getPlotIndex(this));
    }

    public void editnote(AbstractDrawer draw) {
        plot(draw, PlotCanvas.EDIT_COLOR);
        plotLayerPlots(draw, PlotCanvas.EDIT_COLOR);
    }
}
