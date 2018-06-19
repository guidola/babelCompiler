package taulasimbols;

import java.util.Vector;

/**
 * <p>Clase que representa els tipus array del llenguatge Babel.</p>
 */
public class TipusArray extends ITipus {

	/**<p>Tipus dels elements del array</p>*/
	private ITipus tipusElements;

	/**<p>Llista de les dimensions del array</p>*/
	private Vector llistaDimensions = new Vector();

	/**<p>Creador de la clase Tipus array</p>*/
	public TipusArray() {
	}

	/**
	 * <p>Creador de la clase Tipus array</p>
	 * @param (String) nom
	 * @param (int) tamany
	 */
	public TipusArray(String nom, int tamany) {
		super.nom = nom;
		super.tamany = tamany;
	}

	/**
	 * <p>Creador de la clase Tipus array</p>
	 * @param (ITipus) tipusElements
	 */
	public TipusArray(ITipus tipusElements) {
		this.tipusElements = tipusElements;
	}

	/**
	 * <p>Creador de la clase Tipus array</p>
	 * @param (String) nom
	 * @param (int) tamany
	 * @param (ITipus) tipus dels elements del array
	 */
	public TipusArray(String nom, int tamany, ITipus tipusElements) {
		super.nom = nom;
		super.tamany = tamany;
		this.tipusElements = tipusElements;
	}

	/**<p> Obt� el tipus dels elements del array </p>*/
	public ITipus getTipusElements() {
		return tipusElements;
	}

	/**
	 * <p> Determina el tipus dels elements del array </p>
	 * @param (ITipus) tipusElements
	 */
	public void setTipusElements(ITipus tipusElements) {
		this.tipusElements = tipusElements;
	}

	/**
	 * <p> Insereix una nova dimensi� a la llista de dimensions </p>
	 * @param (DimensioArray) dimensio
	 */
	public void inserirDimensio(DimensioArray dimensio) {
		llistaDimensions.add(dimensio);
	}

	/**
	 * <p>Obt� la dimensi� que es troba la posici� que indica el par�metre </p>
	 * @param (int) index
	 * @return DimensioArray
	 */
	public DimensioArray obtenirDimensio(int index) {
		return (DimensioArray) llistaDimensions.get(index);
	}

	/**
	 * <p>Obt� el n�mero de dimensions que cont� el array.</p>
	 * @return int
	 */
	public int getNumeroDimensions() {
		return llistaDimensions.size();
	}

	/**
	 * <p>Compara l'objecte que se li pasa per paramtre amb l'objecte acual,
	 * retorna cert si s�n iguals.</p>
	 * @param (Object)obj
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		if (obj instanceof TipusArray) {
			TipusArray ta = (TipusArray) obj;
			if (tipusElements != null) {
				return tipusElements.equals(ta.getTipusElements())
						&& llistaDimensions.equals(ta.llistaDimensions);
			} else {
				return ta.getTipusElements() == null
						&& llistaDimensions.equals(ta.llistaDimensions);
			}
		} else {
			return false;
		}
	}

	/**
	 * <p>Obt� tota la informaci� del objecte en format XML</p>
	 * @return String
	 */
	public String toXml() {
		String str = "<TipusArray Nom=\"" + nom + "\" Tamany=\"" + tamany + "\"" + 
			" Numerodimensions=\"" + getNumeroDimensions() + "\">";
		str += "<TipusElements>";
		if (tipusElements != null)
			str += tipusElements.toXml();
		str += "</TipusElements>";
		str += "<Dimensions>";
		for (int i = 0; i < getNumeroDimensions(); i++) {
			DimensioArray dim = obtenirDimensio(i);
			str += dim.toXml();
		}
		str += "</Dimensions>";
		str += "</TipusArray>";
		return str;
	}
}
