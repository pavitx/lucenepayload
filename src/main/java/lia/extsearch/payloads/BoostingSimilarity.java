package lia.extsearch.payloads;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.util.BytesRef;


public class BoostingSimilarity extends ClassicSimilarity {

    @Override
    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        if (payload != null) {
            return PayloadHelper.decodeFloat(payload.bytes, 0);
        } else {
            return 1.0F;
        }
    }
}
