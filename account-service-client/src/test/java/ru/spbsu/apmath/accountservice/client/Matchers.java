package ru.spbsu.apmath.accountservice.client;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.nio.ByteBuffer;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 21:43
 */
public class Matchers {
  public static Matcher<ByteBuffer> fromAddAmountWithId(Matcher<Integer> submatcher) {
    return new FeatureMatcher<ByteBuffer, Integer>(submatcher, "bytebuffer from add amount with id ",
            "actual id is ") {
      @Override
      protected Integer featureValueOf(ByteBuffer buffer) {
        assertThat(buffer.limit(), equalTo(12));
        return buffer.getInt();
      }
    };
  }

  public static Matcher<ByteBuffer> fromGetAmountWithId(Matcher<Integer> submatcher) {
    return new FeatureMatcher<ByteBuffer, Integer>(submatcher, "bytebuffer from get amount with id ",
            "actual id is ") {
      @Override
      protected Integer featureValueOf(ByteBuffer buffer) {
        assertThat(buffer.limit(), equalTo(4));
        return buffer.getInt();
      }
    };
  }

  public static Matcher<Integer> elementOf(final List<Integer> list) {
    return new FeatureMatcher<Integer, Boolean>(is(true), "integer from list ", "actual list is ") {
      @Override
      protected Boolean featureValueOf(Integer integer) {
        return list.contains(integer);
      }
    };
  }
}
