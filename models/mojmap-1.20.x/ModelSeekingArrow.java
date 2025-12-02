// Made with Blockbench 5.0.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

public class ModelSeekingArrow<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "seekingarrow"), "main");
	private final ModelPart SeekingArrow;
	private final ModelPart Arrow;
	private final ModelPart left_spike;
	private final ModelPart middle_spike;
	private final ModelPart right_spike;
	private final ModelPart base;
	private final ModelPart Arrow2;
	private final ModelPart left_spike2;
	private final ModelPart middle_spike2;
	private final ModelPart right_spike2;
	private final ModelPart base2;
	private final ModelPart body;

	public ModelSeekingArrow(ModelPart root) {
		this.SeekingArrow = root.getChild("SeekingArrow");
		this.Arrow = this.SeekingArrow.getChild("Arrow");
		this.left_spike = this.Arrow.getChild("left_spike");
		this.middle_spike = this.Arrow.getChild("middle_spike");
		this.right_spike = this.Arrow.getChild("right_spike");
		this.base = this.Arrow.getChild("base");
		this.Arrow2 = this.SeekingArrow.getChild("Arrow2");
		this.left_spike2 = this.Arrow2.getChild("left_spike2");
		this.middle_spike2 = this.Arrow2.getChild("middle_spike2");
		this.right_spike2 = this.Arrow2.getChild("right_spike2");
		this.base2 = this.Arrow2.getChild("base2");
		this.body = this.SeekingArrow.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition SeekingArrow = partdefinition.addOrReplaceChild("SeekingArrow", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Arrow = SeekingArrow.addOrReplaceChild("Arrow", CubeListBuilder.create(),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_spike = Arrow.addOrReplaceChild("left_spike", CubeListBuilder.create().texOffs(4, 8).addBox(
				-2.5F, -3.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition middle_spike = Arrow.addOrReplaceChild("middle_spike", CubeListBuilder.create().texOffs(10, 4)
				.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -31.0F, 0.0F));

		PartDefinition right_spike = Arrow.addOrReplaceChild("right_spike", CubeListBuilder.create().texOffs(6, 8)
				.addBox(1.5F, -3.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition base = Arrow.addOrReplaceChild("base", CubeListBuilder.create().texOffs(4, 0).addBox(-1.5F, 0.0F,
				1.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -29.0F, -1.0F));

		PartDefinition Arrow2 = SeekingArrow.addOrReplaceChild("Arrow2", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition left_spike2 = Arrow2.addOrReplaceChild("left_spike2", CubeListBuilder.create().texOffs(8, 8)
				.addBox(-2.5F, -3.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition middle_spike2 = Arrow2.addOrReplaceChild("middle_spike2", CubeListBuilder.create().texOffs(10, 6)
				.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -31.0F, 0.0F));

		PartDefinition right_spike2 = Arrow2.addOrReplaceChild("right_spike2", CubeListBuilder.create().texOffs(10, 0)
				.addBox(1.5F, -3.0F, 0.0F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -24.0F, 0.0F));

		PartDefinition base2 = Arrow2.addOrReplaceChild("base2", CubeListBuilder.create().texOffs(4, 4).addBox(-1.5F,
				0.0F, 1.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -29.0F, -1.0F));

		PartDefinition body = SeekingArrow.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 2.0F, -0.5F, 1.0F, 25.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -27.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		SeekingArrow.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.SeekingArrow.yRot = ageInTicks;
		this.body.yRot = ageInTicks;
	}
}