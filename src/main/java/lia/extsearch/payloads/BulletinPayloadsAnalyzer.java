package lia.extsearch.payloads;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class BulletinPayloadsAnalyzer extends Analyzer {
    private boolean isBulletin;
    private float boost;

    BulletinPayloadsAnalyzer(float boost) {
        this.boost = boost;
    }

    void setIsBulletin(boolean v) {
        isBulletin = v;
    }
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        BulletinPayloadsFilter result = new BulletinPayloadsFilter(new StandardFilter(source), boost);
        result.setIsBulletin(true);
        return new TokenStreamComponents(source, result);
    }
}
