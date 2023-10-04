package jugglestruggle.timechangerstruggle.client.config.widget;

import jugglestruggle.timechangerstruggle.client.widget.SelfWidgetRender;
import jugglestruggle.timechangerstruggle.client.widget.SelfWidgetRendererInheritor;
import jugglestruggle.timechangerstruggle.config.property.BaseProperty;
import jugglestruggle.timechangerstruggle.config.property.BooleanValue;
import jugglestruggle.timechangerstruggle.config.property.EnumValue;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.minecraft.screen.ScreenTexts;

import com.google.common.collect.ImmutableList;

/**
 * @author JuggleStruggle
 * @implNote Created on 30-Jan-2022, Sunday
 */
@Environment(EnvType.CLIENT)
public class CyclingWidgetConfig<B extends BaseProperty<B, T>, T> extends CyclingButtonWidgetCopy<T>
implements WidgetConfigInterface<B, T>, SelfWidgetRendererInheritor<CyclingWidgetConfig<B, T>>
{
	private final B property;
	private final SelfWidgetRender<CyclingWidgetConfig<B, T>> renderer;
	private T initial;
	/**
	 * This cycling widget config will provide its own callback as to avoid using mixins again,
	 * but that callback will also call this callback :)
	 */
	private final UpdateCallback<T> externalCallback;
	
	protected CyclingWidgetConfig(
			B property, int width, int height, Text message, Text optionText,
			int index, T value, Values<T> values, Function<T, Text> valueToText,
			Function<CyclingButtonWidgetCopy<T>, MutableText> narrationMessageFactory,
			UpdateCallback<T> externalCallback, SimpleOption.TooltipFactory<T> tooltipFactory, boolean optionTextOmitted)
	{
		super(0, 0, width, height, message, optionText, 
			index, value, values, valueToText, 
			narrationMessageFactory, 
			new SetPropertyValueCallback<B, T>(),
			tooltipFactory, optionTextOmitted);

		this.property = property;
		this.initial = property.get();
		
		this.externalCallback = externalCallback;
		this.renderer = new SelfWidgetRender<>(this, null);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public B getProperty() {
		return this.property;
	}
	@Override
	public T getInitialValue() {
		return this.initial;
	}
	@Override
	public void setInitialValue(T value) {
		this.initial = value;
	}
	@Override
	public boolean isDefaultValue() {
		return false;
	}
	@Override
	public void setValue(T value)
	{
		super.setValue(value);
		this.onValueChanged(value);
	}
	private void onValueChanged(T newValue)
	{
		this.property.set(newValue);
		
		if (this.externalCallback != null) {
			this.externalCallback.onValueChange(this, newValue);
		}
	}
	@Override
	public void forceSetWidgetValueToDefault(boolean justInitial) {
		this.setPropertyValueToDefault(justInitial);
	}
	@Override
	public void setPropertyValueToDefault(boolean justInitial)
	{
		if (justInitial)
		{
			if (this.initial != null)
				this.setValue(this.initial);
		}
		else
		{
			final T defaultValue = this.property.getDefaultValue();
			
			if (defaultValue != null)
				this.setValue(defaultValue);
		}
	}
	
	
	
	
	
	
	@Override
	public SelfWidgetRender<CyclingWidgetConfig<B, T>> getWidgetRenderer() {
		return this.renderer;
	}
	@Override
	public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		this.renderer.renderButton(drawContext, mouseX, mouseY, delta);
	}
	
	
	
	
	
	
	public static WidgetConfigBuilderBoolean booleanCycle(BooleanValue property, Text trueText, Text falseText)
	{
		Function<Boolean, Text> valueToText;
		
		final boolean trueTextIsNull = trueText == null;
		final boolean falseTextIsNull = falseText == null;
		
		if (trueTextIsNull && falseTextIsNull)
			valueToText = state -> Text.empty();
		else if (trueTextIsNull)
			valueToText = state -> falseText;
		else if (falseTextIsNull)
			valueToText = state -> trueText;
		else
			valueToText = state -> state ? trueText : falseText;
		
		WidgetConfigBuilderBoolean wcbb = new WidgetConfigBuilderBoolean(
			property, valueToText
		);
		
		wcbb.values(ImmutableList.of(true, false));
		wcbb.initially(property.get());
		
		return wcbb;
	}
	public static <EV extends Enum<EV>> WidgetConfigBuilderEnum<EV> enumCycle(EnumValue<EV> property)
	{
		Function<EV, Text> valueToText = property.getVTT();
		
		if (valueToText == null) {
			valueToText = value -> Text.literal(value.toString());
		}
		
		WidgetConfigBuilderEnum<EV> wcbe = new WidgetConfigBuilderEnum<>(property, valueToText);
		
		wcbe.values(property.getEnumValues());
		wcbe.initially(property.get());
		
		return wcbe;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return getWidth();
	}

	protected static class SetPropertyValueCallback<B extends BaseProperty<B, T>, T> implements UpdateCallback<T>
	{
		protected SetPropertyValueCallback() { }
		
		@Override
		public void onValueChange(CyclingButtonWidgetCopy<T> button, T value) {
			((CyclingWidgetConfig<B, T>)button).onValueChanged(value);
		}
	}
	public static class WidgetConfigBuilder<B extends BaseProperty<B, V>, V> extends CyclingButtonWidgetCopy.Builder<V>
	{
		public final B propertyRepresented;
		
		public WidgetConfigBuilder(B property, Function<V, Text> valueToText) {
			super(valueToText); this.propertyRepresented = property;
		}
		
		@Override
		public Builder<V> initially(V value)
		{
			this.value = value;
			
			int valueIndex = this.values.getDefaults().indexOf(value);
			
			// means that it doesn't exist
			if (valueIndex != -1)
				this.initialIndex = valueIndex;
				
			return this;
		}
		
		public CyclingWidgetConfig<B, V> build(int width, int height, Text optionText) {
			return this.build(width, height, optionText, (b, v) -> {});
		}
		public CyclingWidgetConfig<B, V> build(int width, int height, Text optionText, UpdateCallback<V> callback)
		{
			List<V> defaults = this.values.getDefaults();
			
			V startingValue = this.value;
			startingValue = startingValue == null ? defaults.get(this.initialIndex) : startingValue;
			
			Text messageText = valueToText.apply(startingValue);
			
			if (!this.optionTextOmitted)
				messageText = ScreenTexts.composeGenericOptionText(optionText, messageText);
			
			return new CyclingWidgetConfig<>(this.propertyRepresented, width, height, messageText, optionText,
					this.initialIndex, startingValue, this.values, this.valueToText,
					this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
		}
		
		@Override
		public CyclingButtonWidgetCopy<V> build(int x, int y, int width, int height, Text optionText) {
			return null;
		}
		@Override
		public CyclingButtonWidgetCopy<V> build(int x, int y, int width, int height, Text optionText, UpdateCallback<V> callback) {
			return null;
		}
	}
	public static class WidgetConfigBuilderBoolean extends WidgetConfigBuilder<BooleanValue, Boolean>
	{
		public WidgetConfigBuilderBoolean(BooleanValue property, Function<Boolean, Text> valueToText) {
			super(property, valueToText);
		}
	}
	public static class WidgetConfigBuilderEnum<EV extends Enum<EV>> extends WidgetConfigBuilder<EnumValue<EV>, EV>
	{
		public WidgetConfigBuilderEnum(EnumValue<EV> property, Function<EV, Text> valueToText) {
			super(property, valueToText);
		}
	}
}

