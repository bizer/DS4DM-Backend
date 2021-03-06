package org.webdatacommons.webtables.tools.cleaning;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;

/* Just an example custom Lucene analyzer */
/**
 * 
 * The code was mainly copied from the DWTC framework 
 * (https://github.com/JulianEberius/dwtc-extractor & https://github.com/JulianEberius/dwtc-tools)
 * 
 * @author Robert Meusel (robert@informatik.uni-mannheim.de) - Translation to DPEF
 *
 */
public class CustomAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(final String fieldName,
			final Reader reader) {
		final ClassicTokenizer src = new ClassicTokenizer(reader);
		src.setMaxTokenLength(255);
		TokenStream filter = new ClassicFilter(src);
		filter = new LowerCaseFilter(filter);
		filter = new StopFilter(filter, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		filter = new ASCIIFoldingFilter(filter);
		return new TokenStreamComponents(src, filter) {
			@Override
			protected void setReader(final Reader reader) throws IOException {
				src.setMaxTokenLength(255);
				super.setReader(reader);
			}
		};
	}
}