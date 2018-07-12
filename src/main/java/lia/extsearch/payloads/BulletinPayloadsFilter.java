package lia.extsearch.payloads;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class BulletinPayloadsFilter extends TokenFilter {

    private CharTermAttribute termAtt;
    private PayloadAttribute payloadAttr;
    private boolean isBulletin;
    private BytesRef boostPayload;


    public BulletinPayloadsFilter(TokenStream input, float warningBoost) {
        super(input);
        payloadAttr = addAttribute(PayloadAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        boostPayload = new BytesRef(PayloadHelper.encodeFloat(warningBoost));
    }

    void setIsBulletin(boolean v) {
        isBulletin = v;
    }

    @Override
    final public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            if (isBulletin && termAtt.buffer().equals("warning".toCharArray())) {          // #A
                payloadAttr.setPayload(boostPayload);                        // #A
            } else {
                payloadAttr.setPayload(null);                                // #B
            }
            return true;
        } else {
            return false;
        }
    }
}
