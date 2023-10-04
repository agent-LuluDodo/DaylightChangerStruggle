package jugglestruggle.timechangerstruggle.client.util.render;

import jugglestruggle.timechangerstruggle.TimeChangerStruggle;
import jugglestruggle.timechangerstruggle.client.TimeChangerStruggleClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.fabricmc.fabric.impl.resource.loader.FabricModResourcePack;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormatElement.ComponentType;
import net.minecraft.client.render.VertexFormatElement.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 *
 * @author JuggleStruggle
 * @implNote Created on 20-Feb-2022, Sunday
 */
public class RainbowShader extends ShaderProgram
{
	public static final VertexFormat RAINBOW_SHADER_FORMAT;
	public static final VertexFormatElement FLOAT_GENERIC;
	
	static
	{
		FLOAT_GENERIC = new VertexFormatElement(0, ComponentType.FLOAT, Type.GENERIC, 1);

		ImmutableMap.Builder<String, VertexFormatElement> builder = ImmutableMap.builderWithExpectedSize(2);
		
		builder.put("aPosition", VertexFormats.POSITION_ELEMENT);
		builder.put("aOffset", VertexFormats.POSITION_ELEMENT);
		builder.put("aProgress", RainbowShader.FLOAT_GENERIC);
		
		RAINBOW_SHADER_FORMAT = new VertexFormat(builder.build());
	}
	
	
	
	
	
	
	public final GlUniform strokeWidth;
	public final GlUniform stripeScale;
	public final GlUniform timeOffset;

	public RainbowShader() throws IOException
	{
		super(new ShaderResourceManager(), "rainbow_shader", RainbowShader.RAINBOW_SHADER_FORMAT);
		
		this.timeOffset  = super.getUniform("uTimeOffset");
		this.strokeWidth = super.getUniform("uStrokeWidth");
		this.stripeScale = super.getUniform("uDashCount");
	}

	static class ShaderResourceManager implements ResourceManager
	{
		static final String BASE_LOCATION = "/assets/"+TimeChangerStruggle.MOD_ID+"/";
		
		@Override
		public Optional<Resource> getResource(Identifier id) {
			if (id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE))
			{
				if (id.getPath().contains("shaders/core"))
				{
					InputStream resource = TimeChangerStruggleClient.class
						.getResourceAsStream(BASE_LOCATION + id.getPath());

					if (resource != null) 
					{
						Identifier newId = new Identifier(TimeChangerStruggle.MOD_ID, id.getPath());
						//new ResourceImpl(TimeChangerStruggle.MOD_ID, newId, resource, null);
						return Optional.of(new Resource(new DefaultResourcePackBuilder().build(), () -> resource)); // I don't know what I'm actually supposed to do, but this works - I think
					}
				}
			}
			
			return Optional.empty();
		}

		@Override
		public Set<String> getAllNamespaces() {
			return ImmutableSet.of(TimeChangerStruggle.MOD_ID);
		}

		@Override
		public List<Resource> getAllResources(Identifier id) {
			return null;
		}

		@Override
		public Map<Identifier, Resource> findResources(String startingPath, Predicate<Identifier> pathPredicate) {
			return null;
		}

		@Override
		public Map<Identifier, List<Resource>> findAllResources(String startingPath, Predicate<Identifier> allowedPathPredicate) {
			return null;
		}

		@Override
		public Stream<ResourcePack> streamResourcePacks() {
			return null;
		}
	}
}
