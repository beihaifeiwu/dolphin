package com.freetmp.mbg.merge;

import info.debatty.java.stringsimilarity.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

/**
 * Created by LiuPin on 2015/5/15.
 */
public class SimilarityTest {

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests();

  @Test
  public void testLevenshtein() {
    Levenshtein l = new Levenshtein();

    System.out.println(l.distance("My string", "My $tring"));
  }

  @Test
  public void testWeightedLevenshtein() {
    WeightedLevenshtein wl = new WeightedLevenshtein(
        (c1, c2) -> {
          // t and r are next to each other,
          // let's assign a lower cost to substitution
          if (c1 == 't' && c2 == 'r') {
            return 0.5;
          }
          return 1.0;
        });
    System.out.println(wl.distance("String1", "Srring2"));
  }

  @Test
  public void testDamerau(){
    Damerau d = new Damerau();

    // One transposition
    System.out.println(d.distance("ABCDEF", "ABDCEF"));

    // Transposition of 2 characters that are far from each other
    // => 1 deletion + 1 insertion
    System.out.println(d.distance("ABCDEF", "BCDAEF"));

    // distance and similarity allways produce a result between 0 and 1
    System.out.println(d.distance("ABCDEF", "GHABCDE"));
  }

  @Test
  public void testJaroWinkler(){
    JaroWinkler jw = new JaroWinkler();

    System.out.println(jw.distance("My string", "My $tring"));
    System.out.println(jw.similarity("My string", "My $tring"));
  }

  @Test
  public void testLongestCommonSubsequence(){
    LongestCommonSubsequence lcs = new LongestCommonSubsequence();

    System.out.println(lcs.distance("AGCAT", "GAC"));
  }

  @Test
  public void testNGram(){
    NGram twogram = new NGram(2);

    // Should be 0.41666
    System.out.println(twogram.distance("ABCD", "ABTUIO"));
  }

  @Test
  public void testQGram(){
    QGram dig = new QGram(2);

    // AB BC CD CE
    // 1  1  1  0
    // 1  1  0  1
    // 2 / (3 + 3) = 0.33333
    System.out.println(dig.distance("ABCD", "ABCE"));
  }
}
