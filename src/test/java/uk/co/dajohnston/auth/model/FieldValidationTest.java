package uk.co.dajohnston.auth.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FieldValidationTest {

    @Test
    public void toStringShouldContainFieldNameAndMessage() {
        FieldValidation fieldValidation = new FieldValidation("foo", "bar");
        assertThat(fieldValidation.toString(), is("FieldValidation{field='foo', message='bar'}"));
    }

}