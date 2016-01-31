package zhang.jian.ebaybooks.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import zhang.jian.ebaybooks.R;
import zhang.jian.ebaybooks.constants.Constants;
import zhang.jian.ebaybooks.domainobjects.Book;
import zhang.jian.ebaybooks.network.WebService;
import zhang.jian.ebaybooks.utils.ImageLoader;


public class MainFragment extends Fragment {

    private static final String TAG = "Ebay Books";
    private ImageLoader mImageLoader;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(rootView);
        startStationDetailTask();
        return rootView;
    }

    private void initViews(View rootView) {
        mImageLoader = new ImageLoader(getActivity());
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_book);
    }

    private void startStationDetailTask() {
        if (WebService.networkConnected(getActivity())) {
            new GetBookListTask().execute();
        } else {
            WebService.showNetworkDialog(getActivity());
        }
    }

    private class GetBookListTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return WebService.getJson(Constants.EBAY_BOOK_URL);
        }

        @Override
        protected void onPostExecute(final String jsonData) {
            super.onPostExecute(jsonData);
            mProgressBar.setVisibility(View.GONE);
            List<Book> bookList = new ArrayList<>();
            try {
                bookList = setupBookList(jsonData);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            setupBookListView(bookList);

        }
    }

    private void setupBookListView(List<Book> bookList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BookAdapter adapter = new BookAdapter(bookList);
        mRecyclerView.setAdapter(adapter);
    }

    private List<Book> setupBookList(String jsonData) throws JSONException {
        List<Book> tideList = new ArrayList<>();
        JSONArray jBookArr = new JSONArray(jsonData);
        for (int i = 0; i < jBookArr.length(); i++) {
            JSONObject jBook = jBookArr.getJSONObject(i);
            Book book = new Book();
            if (jBook.isNull("title") == false) {
                book.setTitle(jBook.getString("title"));
            }
            if (jBook.isNull("author") == false) {
                book.setAuthor(jBook.getString("author"));
            }
            if (jBook.isNull("imageURL") == false) {
                book.setImageURL(jBook.getString("imageURL"));
            }
            tideList.add(book);
        }
        return tideList;
    }

    private class BookHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;
        private TextView mAuthorTextView;
        private ImageView mThumbnail;

        public BookHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            mThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
        }

        public void bindTide(Book book) {
            mTitleTextView.setText(book.getTitle());
            if (book.getAuthor() != null) {
                mAuthorTextView.setText("Author: " + book.getAuthor());
            }
            mImageLoader.DisplayImage(book.getImageURL(), mThumbnail);
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> mBooks;

        public BookAdapter(List<Book> books) {
            mBooks = books;
        }

        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder(BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bindTide(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }
    }
}
