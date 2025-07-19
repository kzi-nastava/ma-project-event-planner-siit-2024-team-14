package com.example.eventplanner;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.solutions.CategoryService;

import java.io.IOException;
import java.util.Collection;

import retrofit2.Response;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.eventplanner", appContext.getPackageName());
    }

    @Test
    public void testRetrofit() throws IOException {
        CategoryService service = ClientUtils.retrofit.create(CategoryService.class);

        Response<Collection<Category>> categories = service.getAllCategories().execute();

        if (categories.isSuccessful()) {
            System.out.println(categories.body());
        }
        else {
            fail(categories.message());
        }
    }
}