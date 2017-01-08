/*******************************************************************************
 * Copyright (C) 2013 Turkcell
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package tr.com.turkcellteknoloji.turkcellupdater;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for texts that are displayed on message dialog.
 *
 * @author Ugur Ozmen
 * @see #get(String)
 * @see #KEY_TITLE
 * @see #KEY_MESSAGE
 * @see #KEY_IMAGE_URL
 */
public class MessageDescription extends LocalizedStringMap implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Key for dialog title
     *
     * @see #get(String)
     */
    public final static String KEY_TITLE = "title";

    /**
     * Key for text displayed inside dialog
     *
     * @see #get(String)
     */
    public final static String KEY_MESSAGE = "message";

    /**
     * Key for url of image that displayed in dialog
     *
     * @see #get(String)
     */
    public final static String KEY_IMAGE_URL = "imageUrl";

    /**
     * Key for positive button text of update dialog
     *
     * @see #get(String)
     */
    public final static String KEY_POSITIVE_BUTTON = "positive_button";

    /**
     * Key for negative button text of update dialog
     *
     * @see #get(String)
     */
    public final static String KEY_NEGATIVE_BUTTON = "negative_button";

    MessageDescription(String languageCode, String title, String message, String imageUrl, String positiveButton, String negativeButton) {
        super(languageCode, createMap(title, message, imageUrl, positiveButton, negativeButton));
    }

    MessageDescription(String languageCode, JSONObject jsonObject) {
        super(languageCode, jsonObject);
    }

    MessageDescription(String languageCode, String message) {
        super(languageCode, createMap(message));
    }

    private static Map<String, String> createMap(String title, String message, String imageUrl, String positiveButton, String negativeButton) {
        Map<String, String> result = createMap(message);
        result.put(KEY_TITLE, title);
        result.put(KEY_IMAGE_URL, imageUrl);
        result.put(KEY_POSITIVE_BUTTON, positiveButton);
        result.put(KEY_NEGATIVE_BUTTON, negativeButton);
        return result;
    }

    private static Map<String, String> createMap(String message) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(KEY_MESSAGE, message);
        return result;
    }
}

