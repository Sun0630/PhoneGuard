package com.sx.phoneguard.activities;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.sx.phoneguard.db.dao.BlackNumberDao;
import com.sx.phoneguard.domain.BlackNumberData;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testAdd() {
        for (int i = 0; i < 200; i++) {
            BlackNumberDao dao = new BlackNumberDao(getContext());
            BlackNumberData data = new BlackNumberData("110"+i, BlackNumberData.PHONE);
            dao.add(data);
        }

    }

    public void testFindAll() {
        BlackNumberDao dao = new BlackNumberDao(getContext());
        System.out.println(dao.findAll());

    }
}