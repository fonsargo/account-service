package ru.spbsu.apmath.accountservice.client;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.spbsu.apmath.accountservice.client.impl.AddAmountReader;
import ru.spbsu.apmath.accountservice.client.impl.GetAmountReader;

import java.nio.ByteBuffer;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static ru.spbsu.apmath.accountservice.client.Matchers.elementOf;
import static ru.spbsu.apmath.accountservice.client.Matchers.fromAddAmountWithId;
import static ru.spbsu.apmath.accountservice.client.Matchers.fromGetAmountWithId;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 15.09.2014
 * Time: 21:30
 */
@RunWith(Parameterized.class)
public class ReaderTest {

  private static List<Integer> idList = new ArrayList<Integer>();
  private static Random random;
  private static ByteBuffer byteBuffer;

  private BufferHandler bufferHandler;
  private Matcher<? super ByteBuffer> byteBufferMatcher;

  @BeforeClass
  public static void initList() {
    random = new Random();
    byteBuffer = ByteBuffer.allocate(16);
    for (int i = 0; i < 10; i++)
      idList.add(random.nextInt());
  }

  public ReaderTest(BufferHandler bufferHandler, Matcher<? super ByteBuffer> byteBufferMatcher) {
    this.bufferHandler = bufferHandler;
    this.byteBufferMatcher = byteBufferMatcher;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> testData() {
    return Arrays.asList(new Object[][]{
            {new AddAmountReader(idList), fromAddAmountWithId(elementOf(idList))},
            {new GetAmountReader(idList), fromGetAmountWithId(elementOf(idList))}});
  }

  @Test
  public void addAmountReaderTest() {
    bufferHandler.prepareToWrite(byteBuffer);
    assertThat("Возвращаемый ByteBuffer не соответствует требуемому!", byteBuffer, byteBufferMatcher);
  }
}
