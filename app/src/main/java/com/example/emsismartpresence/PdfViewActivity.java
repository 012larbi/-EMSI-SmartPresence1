package com.example.emsismartpresence;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PdfViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        WebView webView = findViewById(R.id.webViewPdf);
        webView.setWebViewClient(new WebViewClient());  // Pour rester dans l’app
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        String pdfUrl = getIntent().getStringExtra("DOCUMENT_URL");
        if (pdfUrl != null) {
            // Google Docs permet d’afficher un PDF dans une WebView
            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
            webView.loadUrl(googleDocsUrl);
        }
    }
}
