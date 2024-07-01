package com.ferreusveritas.dynamictreesplus.data;

import com.ferreusveritas.dynamictrees.api.data.Generator;
import com.ferreusveritas.dynamictrees.data.provider.DTBlockStateProvider;
import com.ferreusveritas.dynamictreesplus.block.mushroom.CapProperties;
import com.ferreusveritas.dynamictreesplus.block.mushroom.DynamicCapCenterBlock;
import com.ferreusveritas.dynamictreesplus.systems.mushroomlogic.MushroomCapDisc;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class CapCenterStateGenerator implements Generator<DTBlockStateProvider, CapProperties> {

    public static final DependencyKey<DynamicCapCenterBlock> CAP_CENTER = new DependencyKey<>("cap_center");
    public static final DependencyKey<Block> PRIMITIVE_CAP = new DependencyKey<>("primitive_cap");

    @Override
    public void generate(DTBlockStateProvider provider, CapProperties input, Dependencies dependencies) {
        ResourceLocation textureOutLocation = provider.block(ForgeRegistries.BLOCKS.getKey(dependencies.get(PRIMITIVE_CAP)));
        ResourceLocation textureInLocation = new ResourceLocation("block/mushroom_block_inside");
        ResourceLocation outLocation = textureOutLocation;
        ResourceLocation inLocation = textureInLocation;
        if (input.shouldGenerateFaceModels()){
            outLocation = provider.models().modLoc(input.getCapFaceModelName());
            inLocation = provider.models().modLoc(input.getCapInsideFaceModelName());
        }
        ModelFile.ExistingModelFile outFaceModel = provider.models().getExistingFile(
                input.getModelPath(CapProperties.OUTSIDE_FACE).orElse(outLocation)
        );
        ModelFile.ExistingModelFile inFaceModel = provider.models().getExistingFile(
                input.getModelPath(CapProperties.INSIDE_FACE).orElse(inLocation)
        );

        final BlockModelBuilder ageZeroModel = provider.models().getBuilder(input.getCapCenterAgeZeroModelName())
                .parent(provider.models().getExistingFile(input.getCapCenterAgeZeroModelParent()));
        input.addCapCenterAgeZeroTextures(ageZeroModel::texture, textureOutLocation, textureInLocation);

        Integer[] notZeroAges = new Integer[MushroomCapDisc.MAX_RADIUS];
        for (int i=1;i<=MushroomCapDisc.MAX_RADIUS;i++){
            notZeroAges[i-1]=i;
        }

        provider.getMultipartBuilder(dependencies.get(CAP_CENTER))
                .part().modelFile(ageZeroModel)
                .addModel().condition(DynamicCapCenterBlock.AGE, 0)
                .end()

                .part().modelFile(outFaceModel).rotationX(270).uvLock(true)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end()

                .part().modelFile(inFaceModel)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end()
                .part().modelFile(inFaceModel).rotationY(90)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end()
                .part().modelFile(inFaceModel).rotationY(180)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end()
                .part().modelFile(inFaceModel).rotationY(270)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end()
                .part().modelFile(inFaceModel).rotationX(90)
                .addModel().condition(DynamicCapCenterBlock.AGE, notZeroAges)
                .end();
    }

    @Override
    public Dependencies gatherDependencies(CapProperties input) {
        return new Dependencies()
                .append(CAP_CENTER, input.getDynamicCapCenterBlock())
                .append(PRIMITIVE_CAP, input.getPrimitiveCapBlock());
    }

}
