/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package ucm.map;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Plugin Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A plugin refers to a Map that can be substituted  to the parent stub. The binding between the plugin and its  parent stub is defined by the in-connections, the  out-connections, and the instance-values. 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link ucm.map.PluginBinding#getId <em>Id</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getRepetitionCount <em>Repetition Count</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getProbability <em>Probability</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getIn <em>In</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getOut <em>Out</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getPlugin <em>Plugin</em>}</li>
 *   <li>{@link ucm.map.PluginBinding#getPrecondition <em>Precondition</em>}</li>
 * </ul>
 * </p>
 *
 * @see ucm.map.MapPackage#getPluginBinding()
 * @model 
 * @generated
 */
public interface PluginBinding extends EObject {
    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see ucm.map.MapPackage#getPluginBinding_Id()
     * @model 
     * @generated
     */
    String getId();

    /**
     * Sets the value of the '{@link ucm.map.PluginBinding#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId(String value);

    /**
     * Returns the value of the '<em><b>Repetition Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Repetition Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Repetition Count</em>' attribute.
     * @see #setRepetitionCount(int)
     * @see ucm.map.MapPackage#getPluginBinding_RepetitionCount()
     * @model 
     * @generated
     */
    int getRepetitionCount();

    /**
     * Sets the value of the '{@link ucm.map.PluginBinding#getRepetitionCount <em>Repetition Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Repetition Count</em>' attribute.
     * @see #getRepetitionCount()
     * @generated
     */
    void setRepetitionCount(int value);

    /**
     * Returns the value of the '<em><b>Probability</b></em>' attribute.
     * The default value is <code>"1.0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Probability</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Probability</em>' attribute.
     * @see #setProbability(double)
     * @see ucm.map.MapPackage#getPluginBinding_Probability()
     * @model default="1.0"
     * @generated
     */
    double getProbability();

    /**
     * Sets the value of the '{@link ucm.map.PluginBinding#getProbability <em>Probability</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Probability</em>' attribute.
     * @see #getProbability()
     * @generated
     */
    void setProbability(double value);

    /**
     * Returns the value of the '<em><b>In</b></em>' containment reference list.
     * The list contents are of type {@link ucm.map.InBinding}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>In</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>In</em>' containment reference list.
     * @see ucm.map.MapPackage#getPluginBinding_In()
     * @model type="ucm.map.InBinding" containment="true" required="true"
     * @generated
     */
    EList getIn();

    /**
     * Returns the value of the '<em><b>Out</b></em>' containment reference list.
     * The list contents are of type {@link ucm.map.OutBinding}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Out</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Out</em>' containment reference list.
     * @see ucm.map.MapPackage#getPluginBinding_Out()
     * @model type="ucm.map.OutBinding" containment="true" required="true"
     * @generated
     */
    EList getOut();

    /**
     * Returns the value of the '<em><b>Plugin</b></em>' reference.
     * It is bidirectional and its opposite is '{@link ucm.map.Map#getParentStub <em>Parent Stub</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Plugin</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Plugin</em>' reference.
     * @see #setPlugin(Map)
     * @see ucm.map.MapPackage#getPluginBinding_Plugin()
     * @see ucm.map.Map#getParentStub
     * @model opposite="parentStub" required="true"
     * @generated
     */
    Map getPlugin();

    /**
     * Sets the value of the '{@link ucm.map.PluginBinding#getPlugin <em>Plugin</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Plugin</em>' reference.
     * @see #getPlugin()
     * @generated
     */
    void setPlugin(Map value);

    /**
     * Returns the value of the '<em><b>Precondition</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Precondition</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Precondition</em>' containment reference.
     * @see #setPrecondition(Condition)
     * @see ucm.map.MapPackage#getPluginBinding_Precondition()
     * @model containment="true"
     * @generated
     */
    Condition getPrecondition();

    /**
     * Sets the value of the '{@link ucm.map.PluginBinding#getPrecondition <em>Precondition</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Precondition</em>' containment reference.
     * @see #getPrecondition()
     * @generated
     */
    void setPrecondition(Condition value);

} // PluginBinding
