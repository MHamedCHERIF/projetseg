/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package ucm.performance.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import ucm.performance.ArrivalProcess;
import ucm.performance.PerfMeasure;
import ucm.performance.PerformancePackage;
import ucm.performance.Workload;

import urncore.impl.UCMmodelElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workload</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getArrivalPattern <em>Arrival Pattern</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getArrivalParam1 <em>Arrival Param1</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getArrivalParam2 <em>Arrival Param2</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getExternalDelay <em>External Delay</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getValue <em>Value</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getCoeffVarSeq <em>Coeff Var Seq</em>}</li>
 *   <li>{@link ucm.performance.impl.WorkloadImpl#getRespTime <em>Resp Time</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class WorkloadImpl extends UCMmodelElementImpl implements Workload {
    /**
     * The default value of the '{@link #getArrivalPattern() <em>Arrival Pattern</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalPattern()
     * @generated
     * @ordered
     */
    protected static final ArrivalProcess ARRIVAL_PATTERN_EDEFAULT = ArrivalProcess.POISSON_PDF_LITERAL;

    /**
     * The cached value of the '{@link #getArrivalPattern() <em>Arrival Pattern</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalPattern()
     * @generated
     * @ordered
     */
    protected ArrivalProcess arrivalPattern = ARRIVAL_PATTERN_EDEFAULT;

    /**
     * The default value of the '{@link #getArrivalParam1() <em>Arrival Param1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalParam1()
     * @generated
     * @ordered
     */
    protected static final double ARRIVAL_PARAM1_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getArrivalParam1() <em>Arrival Param1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalParam1()
     * @generated
     * @ordered
     */
    protected double arrivalParam1 = ARRIVAL_PARAM1_EDEFAULT;

    /**
     * The default value of the '{@link #getArrivalParam2() <em>Arrival Param2</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalParam2()
     * @generated
     * @ordered
     */
    protected static final double ARRIVAL_PARAM2_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getArrivalParam2() <em>Arrival Param2</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArrivalParam2()
     * @generated
     * @ordered
     */
    protected double arrivalParam2 = ARRIVAL_PARAM2_EDEFAULT;

    /**
     * The default value of the '{@link #getExternalDelay() <em>External Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExternalDelay()
     * @generated
     * @ordered
     */
    protected static final double EXTERNAL_DELAY_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getExternalDelay() <em>External Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExternalDelay()
     * @generated
     * @ordered
     */
    protected double externalDelay = EXTERNAL_DELAY_EDEFAULT;

    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected static final double VALUE_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected double value = VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getCoeffVarSeq() <em>Coeff Var Seq</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoeffVarSeq()
     * @generated
     * @ordered
     */
    protected static final double COEFF_VAR_SEQ_EDEFAULT = 0.0;

    /**
     * The cached value of the '{@link #getCoeffVarSeq() <em>Coeff Var Seq</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoeffVarSeq()
     * @generated
     * @ordered
     */
    protected double coeffVarSeq = COEFF_VAR_SEQ_EDEFAULT;

    /**
     * The cached value of the '{@link #getRespTime() <em>Resp Time</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRespTime()
     * @generated
     * @ordered
     */
    protected EList respTime = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected WorkloadImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return PerformancePackage.eINSTANCE.getWorkload();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ArrivalProcess getArrivalPattern() {
        return arrivalPattern;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setArrivalPattern(ArrivalProcess newArrivalPattern) {
        ArrivalProcess oldArrivalPattern = arrivalPattern;
        arrivalPattern = newArrivalPattern == null ? ARRIVAL_PATTERN_EDEFAULT : newArrivalPattern;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__ARRIVAL_PATTERN, oldArrivalPattern, arrivalPattern));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getArrivalParam1() {
        return arrivalParam1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setArrivalParam1(double newArrivalParam1) {
        double oldArrivalParam1 = arrivalParam1;
        arrivalParam1 = newArrivalParam1;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__ARRIVAL_PARAM1, oldArrivalParam1, arrivalParam1));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getArrivalParam2() {
        return arrivalParam2;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setArrivalParam2(double newArrivalParam2) {
        double oldArrivalParam2 = arrivalParam2;
        arrivalParam2 = newArrivalParam2;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__ARRIVAL_PARAM2, oldArrivalParam2, arrivalParam2));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getExternalDelay() {
        return externalDelay;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExternalDelay(double newExternalDelay) {
        double oldExternalDelay = externalDelay;
        externalDelay = newExternalDelay;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__EXTERNAL_DELAY, oldExternalDelay, externalDelay));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValue(double newValue) {
        double oldValue = value;
        value = newValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__VALUE, oldValue, value));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public double getCoeffVarSeq() {
        return coeffVarSeq;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCoeffVarSeq(double newCoeffVarSeq) {
        double oldCoeffVarSeq = coeffVarSeq;
        coeffVarSeq = newCoeffVarSeq;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PerformancePackage.WORKLOAD__COEFF_VAR_SEQ, oldCoeffVarSeq, coeffVarSeq));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getRespTime() {
        if (respTime == null) {
            respTime = new EObjectWithInverseResolvingEList(PerfMeasure.class, this, PerformancePackage.WORKLOAD__RESP_TIME, PerformancePackage.PERF_MEASURE__DURATION);
        }
        return respTime;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case PerformancePackage.WORKLOAD__URN_LINKS:
                    return ((InternalEList)getUrnLinks()).basicAdd(otherEnd, msgs);
                case PerformancePackage.WORKLOAD__RESP_TIME:
                    return ((InternalEList)getRespTime()).basicAdd(otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case PerformancePackage.WORKLOAD__URN_LINKS:
                    return ((InternalEList)getUrnLinks()).basicRemove(otherEnd, msgs);
                case PerformancePackage.WORKLOAD__RESP_TIME:
                    return ((InternalEList)getRespTime()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case PerformancePackage.WORKLOAD__URN_LINKS:
                return getUrnLinks();
            case PerformancePackage.WORKLOAD__ID:
                return getId();
            case PerformancePackage.WORKLOAD__NAME:
                return getName();
            case PerformancePackage.WORKLOAD__DESCRIPTION:
                return getDescription();
            case PerformancePackage.WORKLOAD__ARRIVAL_PATTERN:
                return getArrivalPattern();
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM1:
                return new Double(getArrivalParam1());
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM2:
                return new Double(getArrivalParam2());
            case PerformancePackage.WORKLOAD__EXTERNAL_DELAY:
                return new Double(getExternalDelay());
            case PerformancePackage.WORKLOAD__VALUE:
                return new Double(getValue());
            case PerformancePackage.WORKLOAD__COEFF_VAR_SEQ:
                return new Double(getCoeffVarSeq());
            case PerformancePackage.WORKLOAD__RESP_TIME:
                return getRespTime();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case PerformancePackage.WORKLOAD__URN_LINKS:
                getUrnLinks().clear();
                getUrnLinks().addAll((Collection)newValue);
                return;
            case PerformancePackage.WORKLOAD__ID:
                setId((String)newValue);
                return;
            case PerformancePackage.WORKLOAD__NAME:
                setName((String)newValue);
                return;
            case PerformancePackage.WORKLOAD__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PATTERN:
                setArrivalPattern((ArrivalProcess)newValue);
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM1:
                setArrivalParam1(((Double)newValue).doubleValue());
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM2:
                setArrivalParam2(((Double)newValue).doubleValue());
                return;
            case PerformancePackage.WORKLOAD__EXTERNAL_DELAY:
                setExternalDelay(((Double)newValue).doubleValue());
                return;
            case PerformancePackage.WORKLOAD__VALUE:
                setValue(((Double)newValue).doubleValue());
                return;
            case PerformancePackage.WORKLOAD__COEFF_VAR_SEQ:
                setCoeffVarSeq(((Double)newValue).doubleValue());
                return;
            case PerformancePackage.WORKLOAD__RESP_TIME:
                getRespTime().clear();
                getRespTime().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case PerformancePackage.WORKLOAD__URN_LINKS:
                getUrnLinks().clear();
                return;
            case PerformancePackage.WORKLOAD__ID:
                setId(ID_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__NAME:
                setName(NAME_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PATTERN:
                setArrivalPattern(ARRIVAL_PATTERN_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM1:
                setArrivalParam1(ARRIVAL_PARAM1_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM2:
                setArrivalParam2(ARRIVAL_PARAM2_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__EXTERNAL_DELAY:
                setExternalDelay(EXTERNAL_DELAY_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__VALUE:
                setValue(VALUE_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__COEFF_VAR_SEQ:
                setCoeffVarSeq(COEFF_VAR_SEQ_EDEFAULT);
                return;
            case PerformancePackage.WORKLOAD__RESP_TIME:
                getRespTime().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case PerformancePackage.WORKLOAD__URN_LINKS:
                return urnLinks != null && !urnLinks.isEmpty();
            case PerformancePackage.WORKLOAD__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case PerformancePackage.WORKLOAD__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case PerformancePackage.WORKLOAD__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case PerformancePackage.WORKLOAD__ARRIVAL_PATTERN:
                return arrivalPattern != ARRIVAL_PATTERN_EDEFAULT;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM1:
                return arrivalParam1 != ARRIVAL_PARAM1_EDEFAULT;
            case PerformancePackage.WORKLOAD__ARRIVAL_PARAM2:
                return arrivalParam2 != ARRIVAL_PARAM2_EDEFAULT;
            case PerformancePackage.WORKLOAD__EXTERNAL_DELAY:
                return externalDelay != EXTERNAL_DELAY_EDEFAULT;
            case PerformancePackage.WORKLOAD__VALUE:
                return value != VALUE_EDEFAULT;
            case PerformancePackage.WORKLOAD__COEFF_VAR_SEQ:
                return coeffVarSeq != COEFF_VAR_SEQ_EDEFAULT;
            case PerformancePackage.WORKLOAD__RESP_TIME:
                return respTime != null && !respTime.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (arrivalPattern: ");
        result.append(arrivalPattern);
        result.append(", arrivalParam1: ");
        result.append(arrivalParam1);
        result.append(", arrivalParam2: ");
        result.append(arrivalParam2);
        result.append(", externalDelay: ");
        result.append(externalDelay);
        result.append(", value: ");
        result.append(value);
        result.append(", coeffVarSeq: ");
        result.append(coeffVarSeq);
        result.append(')');
        return result.toString();
    }

} //WorkloadImpl
