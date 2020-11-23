package com.application.apprem.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.application.apprem.R;
import com.application.apprem.fragments.RecyclerViewFragment;
import com.application.apprem.models.BookInfo;
import com.application.apprem.utils.PreferenceUtil;
import com.application.apprem.utils.TextAppearanceUtility;
import com.application.apprem.workers.ImageDownloaderFragment;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NavUtils;
import androidx.core.widget.NestedScrollView;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

/**
 * Shows Details of the Book selected in the List/GridView
 * Inflates library_book_detail
 */

public class BookDetailActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Bundle Key used for grabbing the Intent's data
    public static final String BOOK_INFO_ITEM_STR_KEY = "BookInfo.Item.Data";
    //Constant used for logs
    private static final String LOG_TAG = BookDetailActivity.class.getSimpleName();
    //Storing references to the View components that are to be updated with the data
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private RatingBar mBookRatingBar;
    private TextView mRatingCountTextView;
    private ImageView mBookImageView;
    private TextView mPagesTextView;
    private TextView mPublisherTextView;
    private TextView mCategoryTextView;
    private TextView mDescriptionTextView;
    private Button mEpubImageButton;
    private Button mPdfImageButton;
    private Button mWebPreviewButton;
    private Button mInfoButton;
    private Button mBuyButton;

    //Storing references to the TextViews with constant Text that are to be hidden based on the data
    private TextView mPublisherSubtitleTextView;
    private TextView mCategorySubtitleTextView;
    private TextView mSamplePreviewsSubtitleTextView;
    private TextView mNotForSaleTextView;


    //Storing the Links pointed to by the respective buttons, retrieved from the BookInfo data
    private String mEpubLink;
    private String mPdfLink;
    private String mPreviewLink;
    private String mInfoLink;
    private String mBuyLink;

    //Intent for the Larger Book Image to be shown in the Book Image Activity
    private Intent mBookImageIntent;

    //Method invoked by the system to create and setup the layout 'R.layout.activity_book_detail_original'
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_book_detail);

        //Displaying the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Retrieving the View Components: START
        mTitleTextView = findViewById(R.id.detail_title_text_id);

        mAuthorTextView = findViewById(R.id.detail_author_text_id);

        mBookRatingBar = findViewById(R.id.detail_ratingbar_id);
        mRatingCountTextView = findViewById(R.id.detail_rating_count_text_id);
        mBookImageView = findViewById(R.id.detail_book_image_id);
        mPagesTextView = findViewById(R.id.detail_pages_text_id);
        mPublisherSubtitleTextView = findViewById(R.id.detail_publisher_section_text_id);
        mPublisherTextView = findViewById(R.id.detail_publisher_text_id);

        mCategorySubtitleTextView = findViewById(R.id.detail_category_section_text_id);
        mCategoryTextView = findViewById(R.id.detail_categories_text_id);

        mDescriptionTextView = findViewById(R.id.detail_description_text_id);
        mSamplePreviewsSubtitleTextView = findViewById(R.id.detail_samples_section_text_id);
        mEpubImageButton = findViewById(R.id.detail_epub_button_id);
        mPdfImageButton = findViewById(R.id.detail_pdf_button_id);
        mWebPreviewButton = findViewById(R.id.detail_web_button_id);

        mInfoButton = findViewById(R.id.detail_info_button_id);

        mBuyButton = findViewById(R.id.detail_buy_button_id);
        mNotForSaleTextView = findViewById(R.id.detail_not_for_sale_text_id);
        //Retrieving the View Components: END


        //Setting the Click Listeners on the Buttons
        mEpubImageButton.setOnClickListener(this);
        mPdfImageButton.setOnClickListener(this);
        mWebPreviewButton.setOnClickListener(this);
        mInfoButton.setOnClickListener(this);
        mBuyButton.setOnClickListener(this);

        //Setting Click Listener onto the ImageView
        mBookImageView.setOnClickListener(this);

        //Handling the Intent data
        handleIntent(getIntent());
    }

    /**
     * Handles the Intent data being passed by the
     * {@link RecyclerViewFragment}
     */
    private void handleIntent(Intent intent) {
        //Retrieving the Item Data passed in the intent
        Parcelable parcelableExtra = intent.getParcelableExtra(BOOK_INFO_ITEM_STR_KEY);
        if (parcelableExtra instanceof BookInfo) {
            //Validating and casting to BookInfo accordingly
            BookInfo itemBookInfo = (BookInfo) parcelableExtra;
            //Updating the Layout based on the BookInfo data
            updateLayout(itemBookInfo);
        }
    }

    /**
     * Updates the layout
     */
    private void updateLayout(BookInfo itemBookInfo) {
        //Updating the Title Text
        updateTitle(itemBookInfo.getTitle(), itemBookInfo.getSubTitle());
        //Updating the Author Text
        updateAuthor(itemBookInfo.getAuthors(getString(R.string.no_authors_found_default_text)));
        //Updating the Book Rating
        updateBookRating(itemBookInfo.getBookRatings(), itemBookInfo.getBookRatingCount());
        //Updating the Book Image
        updateBookImage(itemBookInfo.getImageLinkForDetailInfo());
        //Creating an Intent for the Book Image
        generateBookImageIntent(itemBookInfo.getImageLinkForBookImageInfo());
        //Updating the Pages Information
        updatePagesInfo(itemBookInfo.getPageCount(), itemBookInfo.getBookType());

        //Updating the Publisher Text: START
        String publishedDateStr = "";
        try {
            publishedDateStr = itemBookInfo.getPublishedDateInLongFormat(getString(R.string.no_published_date_default_text));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error occurred while parsing and formatting the Published date", e);
        }
        updatePublisher(itemBookInfo.getPublisher(getString(R.string.no_publisher_found_default_text)), publishedDateStr);
        //Updating the Publisher Text: END

        //Updating the Category Text
        updateCategory(itemBookInfo.getCategories(getString(R.string.no_categories_found_default_text)));
        //Updating the Description Text
        updateDescription(itemBookInfo.getDescription(getString(R.string.no_description_found_default_text)));
        //Updating the Previews
        updatePreviews(itemBookInfo.isSampleAvailable(), itemBookInfo.getEpubLink(), itemBookInfo.getPdfLink(), itemBookInfo.getPreviewLink());
        //Updating the Saleability information
        updateSaleability(itemBookInfo.isForSale(), itemBookInfo.isDiscounted(), itemBookInfo.getListPrice(), itemBookInfo.getRetailPrice(), itemBookInfo.getBuyLink());
    }

    /**
     * Details about the saleability of the book
     *
     * @param isForSale    is a Boolean which tells whether the Book is for Sale or Not in the user's region
     * @param isDiscounted is a Boolean which tells whether the Book is Discounted or not
     * @param listPrice    is the List Price of the Book in User's locale currency format
     * @param retailPrice  is the Retail Price of the Book in User's locale currency format
     * @param buyLink      is the Buy Link for purchasing the Book if the Book is for Sale
     */
    private void updateSaleability(boolean isForSale, boolean isDiscounted, String listPrice, String retailPrice, String buyLink) {
        if (isForSale) {
            //Displaying only the Buy Button with the Price details as the Book is saleable
            //in the user's region
            mBuyLink = buyLink; //Updating the Buy Link on the Button

            //Setting the Visibility to the Buy Button
            mBuyButton.setVisibility(View.VISIBLE);
            mNotForSaleTextView.setVisibility(View.GONE);

            //Setting the Text on the Button
            if (isDiscounted) {
                //When the Book is having a Discount, make the List Price appear with a Strike-through Text
                //on the Button
                String textToSet = listPrice + " " + retailPrice + " Buy";
                TextAppearanceUtility.setStrikethroughText(mBuyButton, textToSet, listPrice);
                TextAppearanceUtility.modifyTextColor(mBuyButton, listPrice, getResources().getColor(android.R.color.holo_green_light));
            } else {
                //When there is no Discount on the Price, show only the Retail Price
                //on the Button
                String textToSet = retailPrice + " Buy";
                mBuyButton.setText(textToSet);
            }

        } else {
            //Hiding the Buy Button and displaying only the 'Not For Sale' Text as the Book is NOT
            //for sale in the user's region
            mBuyButton.setVisibility(View.GONE);
            mNotForSaleTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method that updates the Previews section
     */
    private void updatePreviews(boolean isSampleAvailable, String epubLink, String pdfLink, String previewLink) {
        if (isSampleAvailable) {
            //Updating the Buttons with their Previews when available

            if (TextUtils.isEmpty(epubLink)) {
                //Hiding the Epub Button when Epub Preview is not available
                mEpubImageButton.setVisibility(View.GONE);
            } else {
                //Updating the Epub Button with its Preview link when available
                mEpubLink = epubLink;
            }

            if (TextUtils.isEmpty(pdfLink)) {
                //Hiding the Pdf Button when Pdf Preview is not available
                mPdfImageButton.setVisibility(View.GONE);
            } else {
                //Updating the Pdf Button with its Preview link when available
                mPdfLink = pdfLink;
            }

            //Updating the Web Preview Button with its Preview Link
            mPreviewLink = previewLink;


            //Hiding the Info Button and its related components as Web Preview is available
            mInfoButton.setVisibility(View.GONE);
            //mInfoBorderImageView.setVisibility(View.GONE);

        } else {
            //Updating the Info Button with the Info Link when no previews are available
            //and hiding the Button components related to Sample previews as they are NOT available

            mInfoLink = previewLink;

            //Hiding the components related to Sample previews
            mEpubImageButton.setVisibility(View.GONE);
            mPdfImageButton.setVisibility(View.GONE);
            mWebPreviewButton.setVisibility(View.GONE);
            mSamplePreviewsSubtitleTextView.setVisibility(View.GONE);

        }
    }

    /**
     * Method that updates the Description Text for the Book
     */
    private void updateDescription(String description) {
        //Setting the Description of the Book
        mDescriptionTextView.setText(description);
    }

    /**
     * Method that updates the Category Text
     */
    private void updateCategory(String categories) {
        if (categories.equals(getString(R.string.no_categories_found_default_text))) {
            //Hiding the respective view components when Category info is not available
            mCategorySubtitleTextView.setVisibility(View.GONE);
            mCategoryTextView.setVisibility(View.GONE);
          //  mCategoryBorderImageView.setVisibility(View.GONE);
        } else {
            //Updating the Category info when it is present
            mCategoryTextView.setText(categories);
        }
    }

    /**
     * Method that updates the Publisher Text and its Published Date
     */
    private void updatePublisher(String publisher, String publishedDateStr) {
        if (publisher.equals(getString(R.string.no_publisher_found_default_text))
                && publishedDateStr.equals(getString(R.string.no_published_date_default_text))) {
            //Hiding the respective view components when both the Publisher
            //and the Date info is not available
            mPublisherSubtitleTextView.setVisibility(View.GONE);
            mPublisherTextView.setVisibility(View.GONE);
          //  mPublisherBorderImageView.setVisibility(View.GONE);
        } else {
            //Updating the Publisher info when either of the information is present
            mPublisherTextView.setText(getString(R.string.detail_publisher_section_format, publisher, publishedDateStr));
        }
    }

    /**
     * Method that updates the Number of Pages in the Book along with its Type
     */
    private void updatePagesInfo(int pageCount, String bookType) {
        //Setting the Pages information
        mPagesTextView.setText(getString(R.string.pages_book_type, pageCount, bookType));
    }

    /**
     * Method that updates the Book Image
     */
    private void updateBookImage(String imageLinkForDetailInfo) {
        if (!TextUtils.isEmpty(imageLinkForDetailInfo)) {
            //Loading the Image when link is present
            ImageDownloaderFragment
                    .newInstance(this.getSupportFragmentManager(), mBookImageView.getId())
                    .executeAndUpdate(mBookImageView,
                            imageLinkForDetailInfo,
                            mBookImageView.getId());
        } else {
            //Resetting to the default Book image when link is absent
            mBookImageView.setImageResource(R.drawable.ic_book);
        }
    }

    /**
     * Show Book Cover in full screen
     */
    private void generateBookImageIntent(String imageLinkForBookImageInfo) {
        if (!TextUtils.isEmpty(imageLinkForBookImageInfo)) {
            //Generating the Intent for the Book Image to be shown,
            //when the link to Book Image is present

            //Initializing the Book Image Intent
            mBookImageIntent = new Intent(this, BookImageActivity.class);
            //Passing the Image Link to the Book Image Activity
            mBookImageIntent.putExtra(BookImageActivity.BOOK_INFO_ITEM_IMAGE_STR_KEY, imageLinkForBookImageInfo);

        } else {
            //Setting the Intent for the Book Image to NULL,
            //when the link to Book Image is NOT present
            mBookImageIntent = null;
        }
    }

    /**
     * Method that updates the Book's Rating fields
     *
     * @param bookRatings     is the Book's Star Rating
     * @param bookRatingCount is the Count of Ratings on the Book
     */
    private void updateBookRating(float bookRatings, String bookRatingCount) {
        //Updating the Rating on the RatingBar
        mBookRatingBar.setRating(bookRatings);
        //Setting the Rating Count
        mRatingCountTextView.setText(bookRatingCount);
    }

    /**
     * Method that updates the Author Text
     */
    private void updateAuthor(String authors) {
        //Setting the Author Text
        mAuthorTextView.setText(getString(R.string.by_author_text, authors));
    }

    /**
     * Method that updates the Title Text
     */
    private void updateTitle(String title, String subTitle) {
        if (!TextUtils.isEmpty(subTitle)) {
            //When SubTitle is present

            //Separating the title and its subtitle by a new line character: START
            int indexOfSubtitleStart = TextUtils.indexOf(title, subTitle);
            int indexOfColonForSubtitle = TextUtils.lastIndexOf(title, ':', indexOfSubtitleStart);
            if (indexOfColonForSubtitle == -1) {
                //If the title originally had a subtitle but the separator was a '-' instead of ':'
                indexOfColonForSubtitle = TextUtils.lastIndexOf(title, '-', indexOfSubtitleStart);
            }
            title = title.substring(0, indexOfColonForSubtitle) + "\n" + subTitle;
            //Separating the title and its subtitle by a new line character: END

            //Setting the Title Text with resized text for SubTitle
            TextAppearanceUtility.modifyTextSize(mTitleTextView, title, subTitle, 0.75f);
        } else {
            //Setting the Title Text only when SubTitle is NOT present
            mTitleTextView.setText(title);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Executing based on MenuItem's Id
        if (item.getItemId() == android.R.id.home) {
            //Handling the action bar's home/up button
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mainly used for Buttons in the Details Page
     */
    @Override
    public void onClick(View view) {
        //Executing action based on the View's id
        switch (view.getId()) {
            case R.id.detail_epub_button_id:
                //For the EPUB Preview Button
               // openLink(mEpubLink);

                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.Companion.addKeepAliveExtra(BookDetailActivity.this, customTabsIntent.intent);


                    CustomTabsHelper.Companion.openCustomTab(BookDetailActivity.this, customTabsIntent,
                            Uri.parse(mEpubLink),
                            new WebViewFallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.detail_pdf_button_id:
                //For the PDF Preview Button
                //openLink(mPdfLink);


                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.Companion.addKeepAliveExtra(BookDetailActivity.this, customTabsIntent.intent);


                    CustomTabsHelper.Companion.openCustomTab(BookDetailActivity.this, customTabsIntent,
                            Uri.parse(mPdfLink),
                            new WebViewFallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.detail_web_button_id:
                //For the WEB Preview Button
               // openLink(mPreviewLink);

                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.Companion.addKeepAliveExtra(BookDetailActivity.this, customTabsIntent.intent);


                    CustomTabsHelper.Companion.openCustomTab(BookDetailActivity.this, customTabsIntent,
                            Uri.parse(mPreviewLink),
                            new WebViewFallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.detail_info_button_id:
                //For the Info Button
                //openLink(mInfoLink);

                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.Companion.addKeepAliveExtra(BookDetailActivity.this, customTabsIntent.intent);


                    CustomTabsHelper.Companion.openCustomTab(BookDetailActivity.this, customTabsIntent,
                            Uri.parse(mInfoLink),
                            new WebViewFallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.detail_buy_button_id:
                //For the Buy Button
              //  openLink(mBuyLink);

                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .addDefaultShareMenuItem()
                            .setToolbarColor(PreferenceUtil.getPrimaryColor(this))
                            .setShowTitle(true)
                            .build();

                    CustomTabsHelper.Companion.addKeepAliveExtra(BookDetailActivity.this, customTabsIntent.intent);


                    CustomTabsHelper.Companion.openCustomTab(BookDetailActivity.this, customTabsIntent,
                            Uri.parse(mBuyLink),
                            new WebViewFallback());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.detail_book_image_id:
                //For the Book Image shown
                if (mBookImageIntent != null) {
                    //When the Image is present and the Intent is created
                    //Passing the Intent to the Book Image Activity
                    //to display a bigger picture of the Book
                    startActivity(mBookImageIntent);
                } else {
                    //When the Image is not present for the Book
                    //Displaying a toast to indicate the user that there is no image for the Book
                   // Toast.makeText(this, R.string.no_book_image_msg, Toast.LENGTH_SHORT).show();

                    NestedScrollView scrollView = findViewById(R.id.scrollView);
                    Snackbar.make(scrollView, R.string.no_book_image_msg, Snackbar.LENGTH_LONG)
                            .setAction("Okay", null)
                            .show();

                }
                break;

        }
    }


    //Called by the Activity when it is prepared to be shown
    @Override
    protected void onResume() {
        super.onResume();

    }

}