package com.hextrato.kral.core.schema.neural;

import com.hextrato.kral.core.schema.neural.layer.type.NLLinear;
import com.hextrato.kral.core.util.exception.KException;

public class KLayerOperLinear extends KLayer {

	public KLayerOperLinear(KNeural neural, String oper) throws KException {
		super(neural, oper);
		this._blay = new NLLinear();
	}

}
