package com.mmnaseri.utils.dareader;

/** A factory that can create tokenizers for a given document based on a registry. */
public interface DocumentTokenizerFactory {

  /** Returns a new tokenizer for the given document. */
  DocumentTokenizer create(String document);
}
