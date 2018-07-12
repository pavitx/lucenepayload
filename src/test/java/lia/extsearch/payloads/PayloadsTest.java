package lia.extsearch.payloads;

import lia.common.TestUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.payloads.AveragePayloadFunction;
import org.apache.lucene.queries.payloads.PayloadScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


public class PayloadsTest {

    Directory dir;
    IndexWriter writer;
    BulletinPayloadsAnalyzer analyzer;


    @Before
    public void setUp() throws Exception {
        dir = new RAMDirectory();
        analyzer = new BulletinPayloadsAnalyzer(5.0F);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, indexWriterConfig);
    }

    @After
    public void tearDown() throws Exception {
        writer.close();
    }

    void addDoc(String title, String contents) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("title", title, Field.Store.YES));
        doc.add(new TextField("contents", contents, Field.Store.NO));
        analyzer.setIsBulletin(contents.startsWith("Bulletin:"));
        writer.addDocument(doc);
    }

    @Test
    public void testPayloadTermQuery() throws Throwable {
        addDoc("Hurricane warning",
                "Bulletin: A hurricane warning was issued at " +
                        "6 AM for the outer great banks");
        addDoc("Warning label maker",
                "The warning label maker is a delightful toy for " +
                        "your precocious seven year old's warning needs");
        addDoc("Tornado warning",
                "Bulletin: There is a tornado warning for " +
                        "Worcester county until 6 PM today");
        writer.commit();

        IndexReader r =  DirectoryReader.open(dir);


        IndexSearcher searcher = new IndexSearcher(r);

        searcher.setSimilarity(new BoostingSimilarity());

        Term warning = new Term("contents", "warning");

        Query query1 = new TermQuery(warning);
        System.out.println("\nTermQuery results:");
        TopDocs hits = searcher.search(query1, 10);
        TestUtil.dumpHits(searcher, hits);

        assertEquals("Warning label maker",                                // #B
                searcher.doc(hits.scoreDocs[0].doc).get("title"));    // #B

        /*Query query2 = new PayloadScoreQuery(new SpanQuery() {
            @Override
            public String getField() {
                return null;
            }

            @Override
            public SpanWeight createWeight(IndexSearcher searcher, boolean needsScores) throws IOException {
                return new SpanWeight() {
                    @Override
                    public void extractTermContexts(Map<Term, TermContext> contexts) {

                    }

                    @Override
                    public Spans getSpans(LeafReaderContext ctx, Postings requiredPostings) throws IOException {
                        return null;
                    }

                    @Override
                    public void extractTerms(Set<Term> terms) {

                    }
                };
            }
        },
                new AveragePayloadFunction());*/
        System.out.println("\nPayloadTermQuery results:");
        //hits = searcher.search(query2, 10);
        //TestUtil.dumpHits(searcher, hits);

        //assertEquals("Warning label maker",                                // #C
               // searcher.doc(hits.scoreDocs[2].doc).get("title"));    // #C
        r.close();

    }
}
