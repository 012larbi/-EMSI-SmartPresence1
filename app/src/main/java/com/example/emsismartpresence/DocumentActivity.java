package com.example.emsismartpresence;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentActivity extends AppCompatActivity implements DocumentAdapter.OnDocumentListener {

    private RecyclerView rvDocuments;
    private Button btnToggleView;
    private TextView tvEmpty;

    private DocumentAdapter adapter;
    private List<DocumentItem> documentList = new ArrayList<>();

    private boolean showImages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        rvDocuments = findViewById(R.id.rvDocuments);
        btnToggleView = findViewById(R.id.btnToggleView);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new DocumentAdapter(new ArrayList<>(), this);
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(adapter);

        loadTestDocuments();

        btnToggleView.setOnClickListener(v -> {
            showImages = !showImages;
            btnToggleView.setText(showImages ? "Afficher Images" : "Afficher PDF");
            filterDocuments();
        });
    }

    private void loadTestDocuments() {
        documentList.clear();
        documentList.add(new DocumentItem("1", "Mobile", "https://via.placeholder.com/150.jpg"));
        documentList.add(new DocumentItem("2", "aletier Authentification", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));
        documentList.add(new DocumentItem("3", "aletier Firebase", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));
        documentList.add(new DocumentItem("4", "aletier Gemini Assistant", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));
        documentList.add(new DocumentItem("5", "aletier Firestore", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));
        documentList.add(new DocumentItem("6", "aletier Maps", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));
        documentList.add(new DocumentItem("7", " aletier 9 ", "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"));

        documentList.add(new DocumentItem("8", " interface application ", "https://via.placeholder.com/200.png"));
        filterDocuments();
    }

    private void filterDocuments() {
        List<DocumentItem> filtered = new ArrayList<>();
        for (DocumentItem doc : documentList) {
            String url = doc.getUrl().toLowerCase();
            if (showImages && (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png"))) {
                filtered.add(doc);
            } else if (!showImages && url.endsWith(".pdf")) {
                filtered.add(doc);
            }
        }
        adapter.updateList(filtered);
        updateUI();
    }

    private void updateUI() {
        if (adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvDocuments.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvDocuments.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDocumentClick(int position) {
        DocumentItem doc = adapter.getDocumentAt(position);
        Toast.makeText(this, "Clic sur : " + doc.getTitle(), Toast.LENGTH_SHORT).show();
        // Ici vous pouvez ouvrir une nouvelle activité pour afficher image ou PDF
    }
}
