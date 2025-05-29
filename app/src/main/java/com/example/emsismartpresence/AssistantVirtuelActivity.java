package com.example.emsismartpresence;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AssistantVirtuelActivity extends AppCompatActivity {

    private final String API_KEY = "AIzaSyAnWkP0V9DYP7ldcKyITrO-tit8T6OtAOQ";
    private TextView txtResponse;
    private EditText editMessage;
    private Button btnSend;

    private OkHttpClient client;

    private static final MediaType JSON = MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_virtuel);

        txtResponse = findViewById(R.id.geminianswer);
        editMessage = findViewById(R.id.prompt);
        btnSend = findViewById(R.id.btnSend);

        // Permettre le scroll dans le TextView
        txtResponse.setMovementMethod(new ScrollingMovementMethod());

        client = new OkHttpClient();

        btnSend.setOnClickListener(v -> {
            String userMessage = editMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Ajouter message utilisateur à l'historique
                String currentText = txtResponse.getText().toString();
                String updatedText = currentText + "\n\nVous : " + userMessage;
                txtResponse.setText(updatedText);

                // Vider le champ de saisie
                editMessage.setText("");

                // Envoyer la requête
                sendMessageToGemini(userMessage);
            }
        });
    }

    private void sendMessageToGemini(String message) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject partObject = new JSONObject();

            partObject.put("text", message);
            partsArray.put(partObject);
            contentObject.put("parts", partsArray);
            contentsArray.put(contentObject);

            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        String currentText = txtResponse.getText().toString();
                        txtResponse.setText(currentText + "\n\nErreur réseau : " + e.getMessage());
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray candidates = jsonResponse.getJSONArray("candidates");
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            JSONObject content = firstCandidate.getJSONObject("content");
                            JSONArray parts = content.getJSONArray("parts");
                            JSONObject firstPart = parts.getJSONObject(0);
                            String textResponse = firstPart.getString("text");

                            runOnUiThread(() -> {
                                String currentText = txtResponse.getText().toString();
                                txtResponse.setText(currentText + "\n\nAssistant : " + textResponse);
                            });
                        } catch (JSONException e) {
                            runOnUiThread(() -> {
                                String currentText = txtResponse.getText().toString();
                                txtResponse.setText(currentText + "\n\nErreur lecture JSON : " + e.getMessage());
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            String currentText = txtResponse.getText().toString();
                            txtResponse.setText(currentText + "\n\nErreur API : " + response.code() + " - " + response.message());
                        });
                    }
                }
            });
        } catch (JSONException e) {
            String currentText = txtResponse.getText().toString();
            txtResponse.setText(currentText + "\n\nErreur création JSON : " + e.getMessage());
        }
    }
}
