/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.function.rate.fra;

import static com.opengamma.strata.collect.Guavate.toImmutableSet;
import static com.opengamma.strata.engine.calculations.function.FunctionUtils.toScenarioResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.basics.market.MarketDataKey;
import com.opengamma.strata.basics.market.ObservableKey;
import com.opengamma.strata.engine.calculations.DefaultSingleCalculationMarketData;
import com.opengamma.strata.engine.calculations.function.CalculationSingleFunction;
import com.opengamma.strata.engine.calculations.function.result.ScenarioResult;
import com.opengamma.strata.engine.marketdata.CalculationMarketData;
import com.opengamma.strata.engine.marketdata.FunctionRequirements;
import com.opengamma.strata.finance.rate.fra.ExpandedFra;
import com.opengamma.strata.finance.rate.fra.Fra;
import com.opengamma.strata.finance.rate.fra.FraTrade;
import com.opengamma.strata.function.MarketDataRatesProvider;
import com.opengamma.strata.market.key.DiscountFactorsKey;
import com.opengamma.strata.market.key.IndexRateKey;
import com.opengamma.strata.market.key.MarketDataKeys;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.pricer.rate.fra.DiscountingFraProductPricer;

/**
 * Calculates a result of a {@code FraTrade} for each of a set of scenarios.
 * 
 * @param <T>  the return type
 */
public abstract class AbstractFraFunction<T>
    implements CalculationSingleFunction<FraTrade, ScenarioResult<T>> {

  /**
   * If this is true the value returned by the {@code execute} method will support automatic currency
   * conversion if the underlying results support it.
   */
  private final boolean convertCurrencies;

  /**
   * Creates a new instance which will return results from the {@code execute} method that support automatic
   * currency conversion if the underlying results support it.
   */
  protected AbstractFraFunction() {
    this(true);
  }

  /**
   * Creates a new instance.
   *
   * @param convertCurrencies if this is true the value returned by the {@code execute} method will support
   *   automatic currency conversion if the underlying results support it
   */
  protected AbstractFraFunction(boolean convertCurrencies) {
    this.convertCurrencies = convertCurrencies;
  }

  /**
   * Returns the Fra pricer.
   * 
   * @return the pricer
   */
  protected DiscountingFraProductPricer pricer() {
    return DiscountingFraProductPricer.DEFAULT;
  }

  //-------------------------------------------------------------------------
  @Override
  public FunctionRequirements requirements(FraTrade trade) {
    Fra fra = trade.getProduct();

    // Create a set of all indices referenced by the FRA
    Set<Index> indices = new HashSet<>();

    // The main index is always present
    indices.add(fra.getIndex());

    // The index used for linear interpolation is optional
    fra.getIndexInterpolated().ifPresent(indices::add);

    // Create an key identifying the rate of each index referenced by the FRA
    Set<ObservableKey> indexRateKeys = indices.stream()
        .map(IndexRateKey::of)
        .collect(toImmutableSet());

    // Create an key identifying the forward curve of each index referenced by the FRA
    Set<MarketDataKey<?>> indexCurveKeys = indices.stream()
        .map(MarketDataKeys::indexCurve)
        .collect(toImmutableSet());

    // Create a key identifying the discount factors for the FRA currency
    Set<DiscountFactorsKey> discountFactorsKeys = ImmutableSet.of(DiscountFactorsKey.of(fra.getCurrency()));

    return FunctionRequirements.builder()
        .singleValueRequirements(Sets.union(indexCurveKeys, discountFactorsKeys))
        .timeSeriesRequirements(indexRateKeys)
        .outputCurrencies(fra.getCurrency())
        .build();
  }

  @Override
  public ScenarioResult<T> execute(FraTrade trade, CalculationMarketData marketData) {
    ExpandedFra product = trade.getProduct().expand();
    return IntStream.range(0, marketData.getScenarioCount())
        .mapToObj(index -> new DefaultSingleCalculationMarketData(marketData, index))
        .map(MarketDataRatesProvider::new)
        .map(provider -> execute(product, provider))
        .collect(toScenarioResult(convertCurrencies));
  }


  /**
   * Returns the currency of the FRA.
   *
   * @param target  the FRA that is the target of the calculation
   * @return the currency of the FRA
   */
  @Override
  public Optional<Currency> defaultReportingCurrency(FraTrade target) {
    return Optional.of(target.getProduct().getCurrency());
  }

  // execute for a single trade
  protected abstract T execute(ExpandedFra product, RatesProvider provider);

}
