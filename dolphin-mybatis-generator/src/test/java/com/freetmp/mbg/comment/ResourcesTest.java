package com.freetmp.mbg.comment;

import com.freetmp.mbg.i18n.Resources;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.Locale;

/**
 * Created by pin on 2015/3/3.
 */
public class ResourcesTest {

  @Rule
  public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests();

  @Test
  public void testCopyrightReading() {
    Resources resources = new Resources(CommentGenerator.XMBG_CG_I18N_DEFAULT_PATH + "/Copyrights", Locale.CHINA);
    String copyright = resources.getFormatted("JavaSource", "2012", "2015");

    if (StringUtils.isEmpty(copyright)) return;

    String[] array = copyright.split("\\|");
    System.out.println(array[0]);
    System.out.println();
    for (String str : array) {
      if (str.startsWith("*")) {
        str = " " + str;
      }
      System.out.println(str);
    }
  }

  @Test
  public void testXmlCopyrightReading() {
    Resources resources = new Resources(CommentGenerator.XMBG_CG_I18N_DEFAULT_PATH + "/Copyrights", Locale.CHINA);
    String copyright = resources.getFormatted("XmlSource", "2012", "2015");

    if (StringUtils.isEmpty(copyright)) return;

    String[] array = copyright.split("\\|");
    for (String str : array) {
      if (!str.startsWith("<!--") && !str.startsWith("-->")) {
        str = "    " + str;
      }
      System.out.println(str);
    }
  }
}
