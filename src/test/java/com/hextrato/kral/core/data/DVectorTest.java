package com.hextrato.kral.core.data;

import org.junit.Before;
import org.junit.Test;

import com.hextrato.kral.core.data.struct.DVector;
import com.hextrato.kral.core.util.exception.KException;

public class DVectorTest {

	private DVector vector;
	
    @Before
    public void init() throws Exception {
        this.vector = new DVector( new double[] { 1,1,1,1 } );
    }

    @Test
    public void testVectorMagnitude() throws KException {
    	this.vector = new DVector( new double[] { 1,1,1,1 } );
        double magnitude = vector.magnitude();
        assert ( (magnitude >= 1.9 && magnitude <= 2.1) );
    }

    @Test
    public void testVectorNormalise() throws KException {
        this.vector = new DVector( new double[] { 1,1,1,1 } );
    	System.out.println(vector.toString());
    	this.vector.normalizeByFixedMagnitude(1.0);
    	System.out.println(vector.toString());
        double magnitude = vector.magnitude();
        assert ( (magnitude == 1.0) );
    }
}