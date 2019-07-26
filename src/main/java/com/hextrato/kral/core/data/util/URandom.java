package com.hextrato.kral.core.data.util;

public class URandom {
	
	//
	// internal random values
	//
	
	private static int MAX_RANDOM_VALUES = 10007; // first prime number > 10000
	private static float[] _randomValue = new float[MAX_RANDOM_VALUES];
	private static int _currentRandomSeq = -1;
	
	public static void resetRandomValues() {
		for (int i=0; i<MAX_RANDOM_VALUES; i++) _randomValue[i] = (float)Math.random();
		_currentRandomSeq = 0;
	}

	public static float nextRandom() {
		if (_currentRandomSeq < 0) {
			resetRandomValues();
			_currentRandomSeq = 0;
		}
		int i = _currentRandomSeq;
		_currentRandomSeq++;
		if (_currentRandomSeq >= MAX_RANDOM_VALUES) _currentRandomSeq = 0;
		return _randomValue[i];
	}
	
}
