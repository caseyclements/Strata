/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * <p>
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import com.google.common.collect.Lists;
import com.opengamma.strata.finance.credit.type.CdsConvention;
import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class is immutable and thread-safe.
 */
@BeanDefinition(builderScope = "private")
public final class IsdaCreditCurveParRates implements ImmutableBean, Serializable {

  @PropertyDefinition(validate = "notNull")
  private final String name;

  @PropertyDefinition(validate = "notNull")
  private final Period[] creditCurvePoints;

  @PropertyDefinition(validate = "notNull")
  private final double[] parRates;

  @PropertyDefinition(validate = "notNull")
  private final CdsConvention cdsConvention;

  // TODO the recovery rate is not really a part of the curve, but the data is available along side when
  // TODO as parsing the curves, so it is convenient to put it here for the moment.
  @PropertyDefinition(validate = "notNull")
  private final double recoveryRate;

  public static IsdaCreditCurveParRates of(
      String name,
      Period[] creditCurvePoints,
      double[] parRates,
      CdsConvention cdsConvention,
      double recoveryRate
  ) {
    return new IsdaCreditCurveParRates(
        name,
        creditCurvePoints,
        parRates,
        cdsConvention,
        recoveryRate
    );
  }

  public IsdaCreditCurveParRates parallelShiftParRatesinBps(double shift) {
    double[] shiftedRates = parRates.clone();
    for (int i = 0; i < shiftedRates.length; i++) {
      shiftedRates[i] = shiftedRates[i] + shift;
    }
    return applyShift(shiftedRates);
  }

  public IsdaCreditCurveParRates bucketedShiftParRatesinBps(int index, double shift) {
    double[] shiftedRates = parRates.clone();
    shiftedRates[index] = shiftedRates[index] + shift;
    return applyShift(shiftedRates);
  }

  public int getNumberOfPoints() {
    return creditCurvePoints.length;
  }

  public LocalDate[] getCreditCurveEndDatePoints(LocalDate valuationDate) {
    return Lists
        .newArrayList(creditCurvePoints)
        .stream()
        .map(p -> cdsConvention.calcUnadjustedMaturityDateFromValuationDateOf(valuationDate, p))
        .toArray(LocalDate[]::new);
  }

  private IsdaCreditCurveParRates applyShift(double[] shiftedRates) {
    return IsdaCreditCurveParRates.of(
        name,
        creditCurvePoints.clone(),
        shiftedRates,
        cdsConvention,
        recoveryRate
    );
  }

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    if (creditCurvePoints.length <= 0) {
      throw new IllegalArgumentException("Cannot have zero points");
    }
    if (creditCurvePoints.length != parRates.length) {
      throw new IllegalArgumentException("points do not line up");
    }
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF

  /**
   * The meta-bean for {@code IsdaCreditCurveParRates}.
   *
   * @return the meta-bean, not null
   */
  public static IsdaCreditCurveParRates.Meta meta() {
    return IsdaCreditCurveParRates.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(IsdaCreditCurveParRates.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private IsdaCreditCurveParRates(
      String name,
      Period[] creditCurvePoints,
      double[] parRates,
      CdsConvention cdsConvention,
      double recoveryRate) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(creditCurvePoints, "creditCurvePoints");
    JodaBeanUtils.notNull(parRates, "parRates");
    JodaBeanUtils.notNull(cdsConvention, "cdsConvention");
    JodaBeanUtils.notNull(recoveryRate, "recoveryRate");
    this.name = name;
    this.creditCurvePoints = creditCurvePoints;
    this.parRates = parRates.clone();
    this.cdsConvention = cdsConvention;
    this.recoveryRate = recoveryRate;
    validate();
  }

  @Override
  public IsdaCreditCurveParRates.Meta metaBean() {
    return IsdaCreditCurveParRates.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the name.
   *
   * @return the value of the property, not null
   */
  public String getName() {
    return name;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the creditCurvePoints.
   *
   * @return the value of the property, not null
   */
  public Period[] getCreditCurvePoints() {
    return creditCurvePoints;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the parRates.
   *
   * @return the value of the property, not null
   */
  public double[] getParRates() {
    return (parRates != null ? parRates.clone() : null);
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the cdsConvention.
   *
   * @return the value of the property, not null
   */
  public CdsConvention getCdsConvention() {
    return cdsConvention;
  }

  //-----------------------------------------------------------------------

  /**
   * Gets the recoveryRate.
   *
   * @return the value of the property, not null
   */
  public double getRecoveryRate() {
    return recoveryRate;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      IsdaCreditCurveParRates other = (IsdaCreditCurveParRates) obj;
      return JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getCreditCurvePoints(), other.getCreditCurvePoints()) &&
          JodaBeanUtils.equal(getParRates(), other.getParRates()) &&
          JodaBeanUtils.equal(getCdsConvention(), other.getCdsConvention()) &&
          JodaBeanUtils.equal(getRecoveryRate(), other.getRecoveryRate());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCreditCurvePoints());
    hash = hash * 31 + JodaBeanUtils.hashCode(getParRates());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCdsConvention());
    hash = hash * 31 + JodaBeanUtils.hashCode(getRecoveryRate());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(192);
    buf.append("IsdaCreditCurveParRates{");
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("creditCurvePoints").append('=').append(getCreditCurvePoints()).append(',').append(' ');
    buf.append("parRates").append('=').append(getParRates()).append(',').append(' ');
    buf.append("cdsConvention").append('=').append(getCdsConvention()).append(',').append(' ');
    buf.append("recoveryRate").append('=').append(JodaBeanUtils.toString(getRecoveryRate()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------

  /**
   * The meta-bean for {@code IsdaCreditCurveParRates}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> name = DirectMetaProperty.ofImmutable(
        this, "name", IsdaCreditCurveParRates.class, String.class);
    /**
     * The meta-property for the {@code creditCurvePoints} property.
     */
    private final MetaProperty<Period[]> creditCurvePoints = DirectMetaProperty.ofImmutable(
        this, "creditCurvePoints", IsdaCreditCurveParRates.class, Period[].class);
    /**
     * The meta-property for the {@code parRates} property.
     */
    private final MetaProperty<double[]> parRates = DirectMetaProperty.ofImmutable(
        this, "parRates", IsdaCreditCurveParRates.class, double[].class);
    /**
     * The meta-property for the {@code cdsConvention} property.
     */
    private final MetaProperty<CdsConvention> cdsConvention = DirectMetaProperty.ofImmutable(
        this, "cdsConvention", IsdaCreditCurveParRates.class, CdsConvention.class);
    /**
     * The meta-property for the {@code recoveryRate} property.
     */
    private final MetaProperty<Double> recoveryRate = DirectMetaProperty.ofImmutable(
        this, "recoveryRate", IsdaCreditCurveParRates.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "creditCurvePoints",
        "parRates",
        "cdsConvention",
        "recoveryRate");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -1771294215:  // creditCurvePoints
          return creditCurvePoints;
        case 1157229426:  // parRates
          return parRates;
        case 288334147:  // cdsConvention
          return cdsConvention;
        case 2002873877:  // recoveryRate
          return recoveryRate;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends IsdaCreditCurveParRates> builder() {
      return new IsdaCreditCurveParRates.Builder();
    }

    @Override
    public Class<? extends IsdaCreditCurveParRates> beanType() {
      return IsdaCreditCurveParRates.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------

    /**
     * The meta-property for the {@code name} property.
     *
     * @return the meta-property, not null
     */
    public MetaProperty<String> name() {
      return name;
    }

    /**
     * The meta-property for the {@code creditCurvePoints} property.
     *
     * @return the meta-property, not null
     */
    public MetaProperty<Period[]> creditCurvePoints() {
      return creditCurvePoints;
    }

    /**
     * The meta-property for the {@code parRates} property.
     *
     * @return the meta-property, not null
     */
    public MetaProperty<double[]> parRates() {
      return parRates;
    }

    /**
     * The meta-property for the {@code cdsConvention} property.
     *
     * @return the meta-property, not null
     */
    public MetaProperty<CdsConvention> cdsConvention() {
      return cdsConvention;
    }

    /**
     * The meta-property for the {@code recoveryRate} property.
     *
     * @return the meta-property, not null
     */
    public MetaProperty<Double> recoveryRate() {
      return recoveryRate;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((IsdaCreditCurveParRates) bean).getName();
        case -1771294215:  // creditCurvePoints
          return ((IsdaCreditCurveParRates) bean).getCreditCurvePoints();
        case 1157229426:  // parRates
          return ((IsdaCreditCurveParRates) bean).getParRates();
        case 288334147:  // cdsConvention
          return ((IsdaCreditCurveParRates) bean).getCdsConvention();
        case 2002873877:  // recoveryRate
          return ((IsdaCreditCurveParRates) bean).getRecoveryRate();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------

  /**
   * The bean-builder for {@code IsdaCreditCurveParRates}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<IsdaCreditCurveParRates> {

    private String name;
    private Period[] creditCurvePoints;
    private double[] parRates;
    private CdsConvention cdsConvention;
    private double recoveryRate;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -1771294215:  // creditCurvePoints
          return creditCurvePoints;
        case 1157229426:  // parRates
          return parRates;
        case 288334147:  // cdsConvention
          return cdsConvention;
        case 2002873877:  // recoveryRate
          return recoveryRate;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (String) newValue;
          break;
        case -1771294215:  // creditCurvePoints
          this.creditCurvePoints = (Period[]) newValue;
          break;
        case 1157229426:  // parRates
          this.parRates = (double[]) newValue;
          break;
        case 288334147:  // cdsConvention
          this.cdsConvention = (CdsConvention) newValue;
          break;
        case 2002873877:  // recoveryRate
          this.recoveryRate = (Double) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public IsdaCreditCurveParRates build() {
      return new IsdaCreditCurveParRates(
          name,
          creditCurvePoints,
          parRates,
          cdsConvention,
          recoveryRate);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(192);
      buf.append("IsdaCreditCurveParRates.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("creditCurvePoints").append('=').append(JodaBeanUtils.toString(creditCurvePoints)).append(',').append(' ');
      buf.append("parRates").append('=').append(JodaBeanUtils.toString(parRates)).append(',').append(' ');
      buf.append("cdsConvention").append('=').append(JodaBeanUtils.toString(cdsConvention)).append(',').append(' ');
      buf.append("recoveryRate").append('=').append(JodaBeanUtils.toString(recoveryRate));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
