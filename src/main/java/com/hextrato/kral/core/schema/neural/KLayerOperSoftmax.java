package com.hextrato.kral.core.schema.neural;

import com.hextrato.kral.core.schema.neural.layer.type.NLSoftmax;
import com.hextrato.kral.core.util.exception.KException;

public class KLayerOperSoftmax extends KLayer {

	public KLayerOperSoftmax(KNeural neural, String oper) throws KException {
		super(neural, oper);
		this._blay = new NLSoftmax();
	}

}
