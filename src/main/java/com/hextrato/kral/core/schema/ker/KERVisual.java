package com.hextrato.kral.core.schema.ker;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.schema.graph.KTriple;
import com.hextrato.kral.core.schema.neural.layer.type.NLLinear;
import com.hextrato.kral.core.util.exception.KException;

public class KERVisual extends JFrame{

	private static final long serialVersionUID = 1L;
	protected static final int BULLET_SIZE = 8;
	public static Color DIR_VECTOR_COLOR = new Color(0x40A040);
	public static Color DIR_MATRIX_COLOR = new Color(0x204080);
	public static Color INV_VECTOR_COLOR = new Color(0x4040A0);
	public static Color INV_MATRIX_COLOR = new Color(0x208040);
	public static double CLOSE_DISTANCE = 0.1;

	private KER _ker = null;

    int xScreenSize = 640; // Math.min(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height); // 350;
	int yScreenSize = 640; // Math.min(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height); // 800;
	
    public KERVisual(KER ker) throws KException{
    	this._ker = ker;
    	
        JPanel panel=new JPanel();
        getContentPane().add(panel);
        setSize(xScreenSize,yScreenSize);
        this.setTitle(ker.getName());

        // JButton button = new JButton("press");
        // panel.add(button);
        this.setVisible(true);
    }

    private static final int PAD = 64;
    private static final double Z_AXE_X_FACTOR = 0.3;
    private static final double Z_AXE_Y_FACTOR = 0.1;

    //
    // BASIC SHAPES
    //
    
	public void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2, Paint p) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setPaint(p);
	    int x1Entry = (int)(panelWidth/2 + (panelWidth/2-PAD) * x1); // PAD + i*xInc;
	    int y1Entry = (int)(panelHeight/2 + (panelHeight/2-PAD) * -y1); // h - PAD - scale*dataX[i];
	    int x2Entry = (int)(panelWidth/2 + (panelWidth/2-PAD) * x2); // PAD + i*xInc;
	    int y2Entry = (int)(panelHeight/2 + (panelHeight/2-PAD) * -y2); // h - PAD - scale*dataX[i];
	    g2.draw(new Line2D.Double(x1Entry, y1Entry, x2Entry, y2Entry));
	}

	public void drawLine(Graphics2D g2, double x1, double y1, double z1, double x2, double y2, double z2, Paint p) {
		drawLine(g2,x1+(z1*Z_AXE_X_FACTOR),y1+(z1*Z_AXE_Y_FACTOR),x2+(z2*Z_AXE_X_FACTOR),y2+(z2*Z_AXE_Y_FACTOR),p);
	}

	public void drawSquare(Graphics2D g2, double x1, double y1, double x2, double y2, Paint p) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setPaint(p);
	    int x1Entry = (int)(panelWidth/2 + (panelWidth/2-PAD) * x1); // PAD + i*xInc;
	    int y1Entry = (int)(panelHeight/2 + (panelHeight/2-PAD) * -y1); // h - PAD - scale*dataX[i];
	    int x2Entry = (int)(panelWidth/2 + (panelWidth/2-PAD) * x2); // PAD + i*xInc;
	    int y2Entry = (int)(panelHeight/2 + (panelHeight/2-PAD) * -y2); // h - PAD - scale*dataX[i];
	    g2.fill(new Rectangle2D.Double(Math.min(x1Entry,x2Entry)-4, Math.min(y1Entry,y2Entry)-4, Math.abs(x1Entry-x2Entry)+4,Math.abs(y1Entry-y2Entry)+4));
	}

	public void drawSquare(Graphics2D g2, double x1, double y1, double z1, double x2, double y2, double z2, Paint p) {
		drawSquare(g2, x1+(z1*Z_AXE_X_FACTOR),y1+(z1*Z_AXE_Y_FACTOR),x2+(z2*Z_AXE_X_FACTOR),y2+(z2*Z_AXE_Y_FACTOR), p);
	}
	
	public void drawPoint(Graphics2D g2, double x, double y, String label, Paint p, int size) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setPaint(p);
	    int xEntry = (int)(panelWidth/2 + (panelWidth/2-PAD) * x); // PAD + i*xInc;
	    int yEntry = (int)(panelHeight/2 + (panelHeight/2-PAD) * -y); // h - PAD - scale*dataX[i];
	    g2.fill(new Ellipse2D.Double(xEntry-(size/2), yEntry-(size/2), size, size));
		g2.drawString(label, xEntry+(size/2), yEntry-(size/2));
	}

	public void drawPoint(Graphics2D g2, double x, double y, double z, String label, Paint p, int size) {
		drawPoint(g2, x+(z*Z_AXE_X_FACTOR), y+(z*Z_AXE_Y_FACTOR), label, p, (int)((1-(z/2))*size-((size>=10)?4:0)));
	}

	//
	// PAINT EMBEDDER CONSTITUENTS
	//
	private void paintDirectRelation(Graphics2D g2, KTriple triple) throws KException {
		if (triple.getPola() == true) { // && (triple.getRela().getName().equals(relationName) || "".equals(relationName))) {
			String headName = triple.getHead().getName();
			String headType = triple.getHead().getType();
			String relaName = triple.getRela().getName();
			String tailName = triple.getTail().getName();
			String tailType = triple.getTail().getType();
			// HextraEmbed head = this._ker.embeds().getEmbed(headType+":"+headName);
			KEmbed head = this._ker.embeds().getEmbed(headName);
			KEmbed	rela = this._ker.embeds().getEmbed(relaName);
			// HextraEmbed tail = this._ker.embeds().getEmbed(tailType+":"+tailName);
			KEmbed tail = this._ker.embeds().getEmbed(tailName);
			if (head != null && rela != null && tail != null) {
				double h1x,h1y,h1z;					
				h1x = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(0);
				h1y = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(1);
				h1z = 0; 
				if (this._ker.getDimensions() > 2) h1z = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(2);
				double t1x,t1y,t1z;
				t1x = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(0);
				t1y = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(1);
				t1z = 0; 
				if (this._ker.getDimensions() > 2) t1z = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(2);
				try { 
					rela.nnLayerDir.feedForward(head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0));
				} catch (KException e) {
					System.out.println("GUI+err> "+e.getMessage());
				}
				DVector headResult = rela.nnLayerDir.theOutputValues();
				double h2x,h2y,h2z;
				h2x = headResult.getValue(0); 
				h2y = headResult.getValue(1); 
				h2z = 0; 
				if (this._ker.getDimensions() > 2) h2z = headResult.getValue(2);
				double p1x,p1y,p1z;
				p1x = h2x;
				p1y = h2x;
				p1z = h2z;
				if (this._ker.isProjectionMatrixActive()) {
					try {
						NLLinear projection = new NLLinear();
						projection.setInputSize(this._ker.getDimensions());
						projection.setOutputSize(this._ker.getDimensions());
						projection.setBiasesNull();
						projection.setWeights(rela.nnLayerDir.theWeights());
						projection.feed(rela.nnLayerDir.theInputValues());
						DVector pResult = projection.theOutputValues();
						p1x = pResult.getValue(0);
						p1y = pResult.getValue(1);
						if (this._ker.getDimensions() > 2) p1z = pResult.getValue(2);
						this.drawLine(g2, h1x,h1y,h1z, p1x,p1y,p1z, DIR_MATRIX_COLOR);
						this.drawLine(g2, p1x,p1y,p1z, h2x,h2y,h2z, DIR_VECTOR_COLOR);
					} catch (KException e) {
						System.out.println("GUI+err> "+e.getMessage());
					}
				} else {
					this.drawLine(g2, h1x,h1y,h1z, h2x,h2y,h2z, DIR_VECTOR_COLOR);
				}
				if (headResult.distance(tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0)) > CLOSE_DISTANCE) {
					this.drawLine(g2, h2x,h2y,h2z, t1x,t1y,t1z, Color.RED);
				} else {
					this.drawLine(g2,h2x,h2y,h2z, t1x,t1y,t1z, Color.GREEN);
				}
			}
		}
	}
	
	private void paintInverseRelation(Graphics2D g2, KTriple triple) throws KException {
		if (triple.getPola() == true) { // && (triple.getRela().getName().equals(relationName) || "".equals(relationName))) {
			String headName = triple.getHead().getName();
			String headType = triple.getHead().getType();
			String relaName = triple.getRela().getName();
			String tailName = triple.getTail().getName();
			String tailType = triple.getTail().getType();
			// HextraEmbed tail = this._ker.embeds().getEmbed(headType+":"+headName);
			KEmbed tail = this._ker.embeds().getEmbed(headName);
			KEmbed	rela = this._ker.embeds().getEmbed(relaName);
			// HextraEmbed head = this._ker.embeds().getEmbed(tailType+":"+tailName);
			KEmbed head = this._ker.embeds().getEmbed(tailName);
			if (head != null && rela != null && tail != null) {
				double h1x,h1y,h1z;					
				h1x = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(0);
				h1y = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(1);
				h1z = 0; 
				if (this._ker.getDimensions() > 2) h1z = head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(2);
				double t1x,t1y,t1z;
				t1x = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(0);
				t1y = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(1);
				t1z = 0; 
				if (this._ker.getDimensions() > 2) t1z = tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0).getValue(2);
				try { 
					rela.nnLayerInv.feedForward(head.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0));
				} catch (KException e) {
					System.out.println("GUI+err> "+e.getMessage());
				}
				DVector headResult = rela.nnLayerInv.theOutputValues();
				double h2x,h2y,h2z;
				h2x = headResult.getValue(0); 
				h2y = headResult.getValue(1); 
				h2z = 0; 
				if (this._ker.getDimensions() > 2) h2z = headResult.getValue(2);
				double p1x,p1y,p1z;
				p1x = h2x;
				p1y = h2x;
				p1z = h2z;
				if (this._ker.isProjectionMatrixActive()) {
					try {
						NLLinear projection = new NLLinear();
						projection.setInputSize(this._ker.getDimensions());
						projection.setOutputSize(this._ker.getDimensions());
						projection.setBiasesNull();
						projection.setWeights(rela.nnLayerInv.theWeights());
						projection.feed(rela.nnLayerInv.theInputValues());
						DVector pResult = projection.theOutputValues();
						p1x = pResult.getValue(0);
						p1y = pResult.getValue(1);
						if (this._ker.getDimensions() > 2) p1z = pResult.getValue(2);
						this.drawLine(g2, h1x,h1y,h1z, p1x,p1y,p1z, INV_MATRIX_COLOR);
						this.drawLine(g2, p1x,p1y,p1z, h2x,h2y,h2z, INV_VECTOR_COLOR);
					} catch (KException e) {
						System.out.println("GUI+err> "+e.getMessage());
					}
				} else {
					this.drawLine(g2, h1x,h1y,h1z, h2x,h2y,h2z, INV_VECTOR_COLOR);
				}
				if (headResult.distance(tail.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0)) > CLOSE_DISTANCE) {
					// DO NO DRAW red distance on Inverse relations !!!
					// this.drawLine(g2, h2x,h2y,h2z, t1x,t1y,t1z, Color.RED);
				} else {
					this.drawLine(g2,h2x,h2y,h2z, t1x,t1y,t1z, Color.GREEN);
				}
			}
		}
	}

	private void paintRelations(Graphics2D g2) throws KException {
		//
		// IS THIS CORRECT ?
		//
		// for (Map.Entry<String,HextraTriple> entry : this._ker.getGraph().triples().theList().entrySet()) {
		for (String entry : this._ker.getGraph().triples().theList().keySet()) {
			KTriple triple = this._ker.getGraph().triples().getTriple(entry);
			this.paintDirectRelation(g2, triple);
			if (this._ker.isInverseRelationActive())
				this.paintInverseRelation(g2, triple);
		}
	}

	private void paintEntities(Graphics2D g2) {
		// for (Map.Entry<String,HextraEmbed> entry : this._ker.embeds().theList().entrySet() ) {
		for (String entry : this._ker.embeds().theList().keySet() ) {
			KEmbed embed = this._ker.embeds().getEmbed(entry);
			if (embed.getType().equals(KEmbed.ENTITY)) {
				String entityNickName = entry;
				try {
					entityNickName = embed.getName();
				} catch (KException e) {}
				try {
					entityNickName = _ker.getGraph().entities().getEntity(embed.getName()).getNick();
				} catch (KException e) {}
				DVector ev = embed.representation().getSLR(KEmbed.ENTITY_VECTOR_SLR).getRow(0);
				double xValue = 0; try { xValue = ev.getValue(0); } catch (Exception e) {}
				double yValue = 0; try { yValue = ev.getValue(1); } catch (Exception e) {}
				double zValue = 0; if (this._ker.getDimensions() > 2) try { zValue = ev.getValue(2); } catch (Exception e) {}
				this.drawPoint(g2, xValue, yValue, zValue, entityNickName, Color.BLUE, BULLET_SIZE);
			}
		}	
	}
	
	//
	// MAIN PAINT
	//
	
    public void paint(Graphics g) {
    	if (!this.isVisible()) return;
        Graphics2D g2 = (Graphics2D)g;
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, panelWidth, panelHeight);
        g2.setPaint(Color.lightGray);
        g2.fill(new Ellipse2D.Double(PAD, PAD, panelWidth-2*PAD, panelHeight-2*PAD));
        g2.setPaint(Color.darkGray);
        g2.drawOval(PAD, PAD, panelWidth-2*PAD, panelHeight-2*PAD);
        
        // Draw coordinate axis
        g2.setPaint(Color.BLACK);
        drawLine(g2,0,-1,0,+1,Color.BLACK);
        drawLine(g2,-1,0,+1,0,Color.BLACK);
        drawLine(g2,0,0,-1,0,0,+1,Color.GRAY);

        // ...
        // Embedder Entity SLR
        try {
			this.paintRelations(g2);
		} catch (KException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        this.paintEntities(g2);

    }

}

