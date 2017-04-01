package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;

    @BindView(R.id.detail_symbol)
    TextView viewDetailSymbol;
    @BindView(R.id.detail_stock_exchange)
    TextView viewDetailStockExchange;
    @BindView(R.id.detail_price)
    TextView viewDetailPrice;
    @BindView(R.id.detail_absolute_change)
    TextView viewDetailAbsoluteChange;
    @BindView(R.id.detail_percentage_change)
    TextView viewDetailPercentageChange;
    @BindView(R.id.detail_chart)
    LineChart viewDetailChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this); //Butterknife perform binding

        mUri = getIntent().getData();

        if (mToolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    this,
                    mUri,
                    Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final DecimalFormat dollarFormatWithPlus;
        final DecimalFormat dollarFormat;
        final DecimalFormat percentageFormat;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        if (data != null && data.moveToFirst()) {
            viewDetailSymbol.setText(data.getString(Quote.POSITION_NAME));
            viewDetailStockExchange.setText(data.getString(Quote.POSITION_STOCK_EXCHANGE));
            viewDetailPrice.setText(dollarFormat.format(data.getDouble(Quote.POSITION_PRICE)));
            viewDetailAbsoluteChange.setText(dollarFormatWithPlus.format(data.getFloat(Quote.POSITION_ABSOLUTE_CHANGE)));
            viewDetailPercentageChange.setText(percentageFormat.format(data.getFloat(Quote.POSITION_PERCENTAGE_CHANGE)));

            List<String> listHistoryData = Arrays.asList(data.getString(Quote.POSITION_HISTORY).split("\n"));
            //reverse order because the current list is in backward order
            Collections.reverse(listHistoryData);

            List<Entry> entries = new ArrayList<>();

            for (String historyData : listHistoryData) {
                List<String> coordinates = Arrays.asList(historyData.split(","));
                entries.add(new Entry(Float.valueOf(coordinates.get(0)), Float.valueOf(coordinates.get(1))));
            }

            LineDataSet dataSet = new LineDataSet(entries, data.getString(Quote.POSITION_SYMBOL)); // add entries to dataset
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            LineData lineData = new LineData(dataSet);

            viewDetailChart.setData(lineData);
            viewDetailChart.getLegend().setTextColor(Color.WHITE);
            viewDetailChart.getDescription().setEnabled(false);
            viewDetailChart.setExtraOffsets(0, 0, 25, 0);

            viewDetailChart.getXAxis().setTextColor(Color.WHITE);
            viewDetailChart.getXAxis().setLabelCount(6, true);
            viewDetailChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    DateFormat sdf = new SimpleDateFormat("d MMM yy", Locale.getDefault());
                    Date netDate = (new Date((long) value));
                    return sdf.format(netDate);
                }
            });

            viewDetailChart.getAxisLeft().setTextColor(Color.WHITE);
            viewDetailChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return dollarFormat.format(value);
                }
            });
            viewDetailChart.getAxisRight().setDrawLabels(false);

            viewDetailChart.invalidate(); // refresh
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
