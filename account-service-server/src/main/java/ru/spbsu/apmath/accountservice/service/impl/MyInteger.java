package ru.spbsu.apmath.accountservice.service.impl;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Афонин Сергей (hrundelb@yandex.ru)
 * Date: 18.09.2014
 * Time: 0:53
 */
public class MyInteger {
  private static final Random random = new Random();
  private static final double a = random.nextDouble();
  private static final double m = Math.pow(2, random.nextInt(7) + 4);

  private int value;

  public MyInteger(int value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Integer) {
      return value == ((Integer)obj).intValue();
    }
    return false;
  }

  @Override
  public int hashCode() {
    double d = a * value;
    return (int)((d - (int)d) * m);
  }
}
