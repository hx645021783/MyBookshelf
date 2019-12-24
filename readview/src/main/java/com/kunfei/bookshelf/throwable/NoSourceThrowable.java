package com.kunfei.bookshelf.throwable;

public class NoSourceThrowable extends Throwable {
  public   NoSourceThrowable(String tag) {
        super(String.format("%s没有找到书源配置", tag));
    }
}