package havis.custom.harting.itemchecker.ui.handheld.widgets;

/**
 * 
 * An instance of this class is also passed by switching panel, and contains
 * parameters that can be exchanged between activatables
 * {@see havis.application.custom.ai.ui.utils.HavisActivatable}.
 * 
 */
public class HavisParameter {
	private HavisActivateableComposite from;
	private Object parameter;

	/**
	 * Package Constructor. Can only be instantiated by package classes.
	 * 
	 * @param from
	 *            {@link havis.application.custom.ai.ui.utils.HavisActivatable}
	 *            which was called the next
	 *            {@link havis.application.custom.ai.ui.utils.HavisActivatable}
	 * @param parameter
	 *            Parameter for the called
	 *            {@link havis.application.custom.ai.ui.utils.HavisActivatable}
	 */
	<T> HavisParameter(HavisActivateableComposite from, T parameter) {
		this.from = from;
		this.parameter = parameter;
	}

	public HavisActivateableComposite getFrom() {
		return from;
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter() {
		return (T) parameter;
	}
}
