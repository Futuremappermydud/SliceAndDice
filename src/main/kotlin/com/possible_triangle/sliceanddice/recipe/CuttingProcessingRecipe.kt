package com.possible_triangle.sliceanddice.recipe

import com.google.gson.JsonObject
import com.possible_triangle.sliceanddice.Content
import com.possible_triangle.sliceanddice.SliceAndDice
import com.possible_triangle.sliceanddice.compat.jei.CuttingProcessingSubCategory
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory
import com.simibubi.create.content.processing.basin.BasinRecipe
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import java.util.function.Supplier
import javax.annotation.Nonnull

data class CuttingProcessingRecipe(
    val params: ProcessingRecipeParams,
    val tool: Ingredient? = null,
    val converted: Boolean = false
) :
    BasinRecipe(CuttingProcessingRecipe, params), IAssemblyRecipe {

    companion object : IRecipeTypeInfo {
        override fun getId() = ResourceLocation.fromNamespaceAndPath(SliceAndDice.MOD_ID, "cutting")

        override fun <T : RecipeSerializer<*>?> getSerializer() = Content.CUTTING_SERIALIZER.get() as T

        override fun <I : RecipeInput, R : Recipe<I>> getType(): RecipeType<R> = Content.CUTTING_RECIPE_TYPE.unwrap() as RecipeType<R>
    }

    override fun matches(input: RecipeInput, @Nonnull worldIn: Level) = true

    override fun getDescriptionForAssembly(): Component {
        return Component.translatable("${SliceAndDice.MOD_ID}.recipe.assembly.slicer")
    }

    override fun addRequiredMachines(machines: MutableSet<ItemLike>) {
        machines.add(Content.SLICER_BLOCK)
    }

    override fun addAssemblyIngredients(ingredients: List<Ingredient>) {
        // Nothing to do here
    }

    override fun getJEISubCategory(): Supplier<Supplier<SequencedAssemblySubCategory>> {
        return Supplier { Supplier { CuttingProcessingSubCategory() } }
    }

    override fun getMaxInputCount() = 1

    object Serializer : RecipeSerializer<CuttingProcessingRecipe> {

        private val processing = ProcessingRecipeSerializer<CuttingProcessingRecipe>(::CuttingProcessingRecipe)

        override fun fromJson(
            id: ResourceLocation,
            json: JsonObject
        ): CuttingProcessingRecipe {
            val tool = Ingredient.fromJson(json.get("tool"))
            return processing.fromJson(id, json).copy(tool = tool)
        }

        override fun fromNetwork(
            id: ResourceLocation,
            buffer: FriendlyByteBuf
        ): CuttingProcessingRecipe {
            val recipe = processing.fromNetwork(id, buffer)
            val tool = Ingredient.fromNetwork(buffer)
            return recipe.copy(tool = tool)
        }

        override fun toNetwork(
            buffer: FriendlyByteBuf,
            recipe: CuttingProcessingRecipe
        ) {
            processing.toNetwork(buffer, recipe)
            recipe.tool?.toNetwork(buffer)
        }

    }

}