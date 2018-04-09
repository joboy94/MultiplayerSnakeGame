package graphics;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import game.Entity;
import game.Food;
import game.Snake;
import math.Transform;
import math.Vector3f;

public class Renderer 
{
	private final Camera activeCamera;
	private final Shader activeShader;
	
	public Renderer(Camera activeCamera, Shader activeShader)
	{
		this.activeCamera = activeCamera;
		this.activeShader = activeShader;
	}

	/**
	 * Bind vertex array object of entity to render
	 * @param rawQuad RawQuad of the entity to render
	 */
	private void bindModel(RawQuad rawQuad)
	{
		GL30.glBindVertexArray(rawQuad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
	}
	
	/**
	 * Unbind vertex array object of entity to render
	 */
	private void unbindModel()
	{
		GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
	}
	
	/**
	 * Load the Model, View and Projection matrices in the shader
	 * @param entity
	 */
	private void loadTransformationMatrices(Entity entity)
	{
		activeShader.loadTransformationMatrix(Transform.getTransformation(entity.getPosition(), entity.getScale()));
		activeShader.loadProjectionMatrix(Transform.getProjectionMatrix());
		activeShader.loadViewMatrix(activeCamera);
	}
	
	/**
	 * Render the entities in batch(Same rawQuad)
	 * @param entitiesToRender The list of entities to render
	 */
	public void renderEntities(List<Entity> entitiesToRender)
	{
		if (entitiesToRender.isEmpty())
			return;
		
		//
		activeShader.start();
		RawQuad rawQuad = entitiesToRender.get(0).getRawQuad();
		bindModel(rawQuad);
		
		int numberOfBodyParts;
		List<Entity> parts;
		final int vertexCount = rawQuad.getVertexCount();
		
		for (Entity entity : entitiesToRender)
		{
			
			//Render parts of the entity
			parts = entity.getBody();
			numberOfBodyParts = parts.size();
			activeShader.loadColor(entity.getColor());

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getTexture().getId());
			for (int i = 0; i < numberOfBodyParts; i++)
			{
				entity = parts.get(i);
				loadTransformationMatrices(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
			}
		}
		unbindModel();
		activeShader.stop();
	}
	
	/**
	 * Clear the screen buffer before drawing again
	 */
	public void prepare()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
}