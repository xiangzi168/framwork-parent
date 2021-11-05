import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Ognl {
	/**
	 * 可以用于判断 Map,Collection,String,Array是否为空
	 *
	 * @param o java.lang.Object.
	 * @return boolean.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
		if (o == null)
			return true;
		if (o instanceof String) {
			if (((String) o).trim().length() == 0) {
				return true;
			}
		} else if (o instanceof Collection) {
			if (((Collection) o).isEmpty()) {
				return true;
			}
		} else if (o.getClass().isArray()) {
			if (((Object[]) o).length == 0) {
				return true;
			}
		} else if (o instanceof Map) {
			if (((Map) o).isEmpty()) {
				return true;
			}
		} else {
			return false;
		}
		return false;

	}

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空
	 *
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	public static boolean isNotEmpty(Long o) {
		return !isEmpty(o);
	}

	/**
	 * 判断是否为数字
	 *
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (o == null)
			return false;
		if (o instanceof Number) {
			return true;
		}
		if (o instanceof String) {
			try {
				Double.parseDouble((String) o);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * <pre>
	 * 判断两个值是否相等
	 * </pre>
	 *
	 * @return
	 */
	public static boolean equalsValue(String str1, String str2) {
		return String.valueOf(str1).equals(str2);
	}

	/**
	 * <pre>
	 * 判断两个值是否相等
	 * </pre>
	 *
	 * @return
	 */
	public static boolean equalsValueIgnoreCase(String str1, String str2) {
		return String.valueOf(str1).equalsIgnoreCase(str2);
	}

	/**
	 * <pre>
	 * 判断两个值是否不相等
	 * </pre>
	 * </pre>
	 * @return
	 */
	public static boolean notEqualsValue(String str1, String str2) {
		return !equalsValue(str1, str2);
	}

	public static void main(String[] args) {
		System.out.println(new Date());

	}

}
