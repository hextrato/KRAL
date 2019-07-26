package com.hextrato.kral.core.schema.neural;

import com.hextrato.kral.core.schema.neural.layer.type.NLLinear;
import com.hextrato.kral.core.util.exception.KException;

public class KLayerOperLstm extends KLayer {

	public KLayerOperLstm(KNeural neural, String oper) throws KException {
		super(neural, oper);
		this._blay = new NLLinear();
	}

}
