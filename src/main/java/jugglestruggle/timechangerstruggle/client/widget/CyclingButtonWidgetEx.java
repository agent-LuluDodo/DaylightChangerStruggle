package jugglestruggle.timechangerstruggle.client.widget;

import jugglestruggle.timechangerstruggle.client.config.widget.CyclingButtonWidgetCopy;
import jugglestruggle.timechangerstruggle.client.config.widget.CyclingWidgetConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import net.minecraft.screen.ScreenTexts;

import com.google.common.collect.ImmutableList;

/**
 * See {@link CyclingWidgetConfig}'s description.
 * 
 * <p> This is a copy of CyclingWidgetConfig which has removed certain
 * config/property-related settings for general use
 *
 * @author JuggleStruggle
 * @implNote Created on 13-Feb-2022, Sunday
 */
@Environment(EnvType.CLIENT)
public class CyclingButtonWidgetEx<T> extends CyclingButtonWidgetCopy<T>
implements SelfWidgetRendererInheritor<CyclingButtonWidgetEx<T>>
{
	private final SelfWidgetRender<CyclingButtonWidgetEx<T>> renderer;
//	private T initial;
	
	CyclingButtonWidgetEx(
			int width, int height, Text message, Text optionText,
			int index, T value, Values<T> values, Function<T, Text> valueToText,
			Function<CyclingButtonWidgetCopy<T>, MutableText> narrationMessageFactory,
			UpdateCallback<T> callback, SimpleOption.TooltipFactory<T> tooltipFactory, boolean optionTextOmitted
	) {
		super(0, 0, width, height, message, optionText,
				index, value, values, valueToText,
				narrationMessageFactory, callback,
				tooltipFactory, optionTextOmitted);
		
//		this.initial = value;
		this.renderer = new SelfWidgetRender<>(this, null);
	}

	
	
	
	@Override
	public SelfWidgetRender<CyclingButtonWidgetEx<T>> getWidgetRenderer() {
		return this.renderer;
	}
	@Override
	public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		this.renderer.renderButton(drawContext, mouseX, mouseY, delta);
	}
	
	
	
	
	
	
	public static WidgetBuilder<Boolean> booleanCycle(boolean initial, Text trueText, Text falseText)
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
			valueToText = state -> state? trueText : falseText;
		
		WidgetBuilder<Boolean> wcbb = new WidgetBuilder<>(valueToText);
		
		wcbb.values(ImmutableList.of(true, false));
		wcbb.initially(initial);
		
		return wcbb;
	}
	
	
	
	
	

	public static class WidgetBuilder<V> extends CyclingButtonWidgetCopy.Builder<V>
	{
		public WidgetBuilder(Function<V, Text> valueToText) {
			super(valueToText); 
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public Builder<V> initially(V value)
		{
			this.value = value;
			
			int valueIndex = values.getDefaults().indexOf(value);
			
			// means that it doesn't exist
			if (valueIndex != -1)
				this.initialIndex = valueIndex;
				
			return this;
		}
		
		public CyclingButtonWidgetEx<V> build(int width, int height, Text optionText) {
			return this.build(width, height, optionText, (b, v) -> {});
		}
		public CyclingButtonWidgetEx<V> build(int width, int height, Text optionText, UpdateCallback<V> callback)
		{
			List<V> defaults = this.values.getDefaults();
			
			V startingValue = this.value;
			startingValue = startingValue == null ? defaults.get(this.initialIndex) : startingValue;
			
			Text messageText = this.valueToText.apply(startingValue);
			
			if (!this.optionTextOmitted)
				messageText = ScreenTexts.composeGenericOptionText(optionText, messageText);
			
			return new CyclingButtonWidgetEx<>(width, height, messageText, optionText, 
				this.initialIndex, startingValue, this.values, this.valueToText,
				this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
		}
		
		@Override
		@Deprecated
		public CyclingButtonWidgetCopy<V> build(int x, int y, int width, int height, Text optionText) {
			return null;
		}
		@Override
		@Deprecated
		public CyclingButtonWidgetCopy<V> build(int x, int y, int width, int height, Text optionText, UpdateCallback<V> callback) {
			return null;
		}
	}
}

