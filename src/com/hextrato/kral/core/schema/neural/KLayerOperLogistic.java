package com.hextrato.kral.core.schema.neural;

import com.hextrato.kral.core.schema.neural.layer.type.NLLogistic;
import com.hextrato.kral.core.util.exception.KException;

public class KLayerOperLogistic extends KLayer {

	public KLayerOperLogistic(KNeural neural, String oper) throws KException {
		super(neural, oper);
		this._blay = new NLLogistic();
	}

}
