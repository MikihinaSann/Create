import os, re, sys

ROOT = 'src/main/java'
PL = 'io.github.fabricators_of_create.porting_lib'

# exact import-string -> replacement (applied to 'import X;' and 'import static X;')
MAP = {
    'net.neoforged.neoforge.common.Tags': f'{PL}.tags.Tags',
    'net.neoforged.neoforge.common.Tags.Items': f'{PL}.tags.Tags.Items',
    'net.neoforged.neoforge.common.Tags.Blocks': f'{PL}.tags.Tags.Blocks',
    'net.neoforged.neoforge.common.Tags.Fluids': f'{PL}.tags.Tags.Fluids',
    'net.neoforged.neoforge.common.Tags.EntityTypes': f'{PL}.tags.Tags.EntityTypes',
    'net.neoforged.neoforge.common.ModConfigSpec': f'{PL}.config.ModConfigSpec',
    'net.neoforged.neoforge.common.ModConfigSpec.ConfigValue': f'{PL}.config.ModConfigSpec.ConfigValue',
    'net.neoforged.neoforge.common.ModConfigSpec.Builder': f'{PL}.config.ModConfigSpec.Builder',
    'net.neoforged.neoforge.common.conditions.ICondition': f'{PL}.conditions.ICondition',
    'net.neoforged.neoforge.common.conditions.WithConditions': f'{PL}.conditions.WithConditions',
    'net.neoforged.neoforge.common.data.ExistingFileHelper': f'{PL}.data.ExistingFileHelper',
    'net.neoforged.neoforge.common.util.INBTSerializable': f'{PL}.core.util.INBTSerializable',
    'net.neoforged.neoforge.common.SpecialPlantable': f'{PL}.common.util.IPlantable',
    'net.neoforged.neoforge.entity.IEntityWithComplexSpawn': f'{PL}.entity.IEntityWithComplexSpawn',
    'net.neoforged.neoforge.server.command.EnumArgument': f'{PL}.command.EnumArgument',
    'net.neoforged.neoforge.server.ServerLifecycleHooks': f'{PL}.core.util.ServerLifecycleHooks',
    'net.neoforged.neoforge.registries.DeferredRegister': f'{PL}.util.DeferredRegister',
    'net.neoforged.neoforge.registries.DeferredHolder': f'{PL}.util.DeferredHolder',
    'net.neoforged.neoforge.items.IItemHandlerModifiable': f'{PL}.transfer.item.SlottedStackStorage',
    'net.neoforged.neoforge.items.IItemHandler': f'{PL}.transfer.item.SlottedStackStorage',
    'net.neoforged.neoforge.items.ItemStackHandler': 'com.simibubi.create.infrastructure.fabric.transfer.item.ItemStackHandler',
    'net.neoforged.neoforge.items.ItemHandlerHelper': f'{PL}.transfer.item.ItemHandlerHelper',
    'net.neoforged.neoforge.items.wrapper.RecipeWrapper': f'{PL}.transfer.item.RecipeWrapper',
    'net.neoforged.api.distmarker.OnlyIn': 'net.fabricmc.api.Environment',
    'net.neoforged.api.distmarker.Dist': 'net.fabricmc.api.EnvType',
    'net.neoforged.neoforge.event.entity.player.PlayerEvent': f'{PL}.entity.events.player.PlayerEvent',
    'net.neoforged.neoforge.event.entity.player.AttackEntityEvent': f'{PL}.entity.events.player.AttackEntityEvent',
    'net.neoforged.neoforge.event.entity.player.PlayerInteractEvent': f'{PL}.entity.events.player.PlayerInteractEvent',
    'net.neoforged.neoforge.event.tick.EntityTickEvent': f'{PL}.entity.events.tick.EntityTickEvent',
    'net.neoforged.neoforge.event.tick.PlayerTickEvent': f'{PL}.entity.events.tick.PlayerTickEvent',
    'net.neoforged.neoforge.event.level.BlockDropsEvent': f'{PL}.level.events.BlockDropsEvent',
    'net.neoforged.neoforge.event.level.BlockEvent': f'{PL}.level.events.BlockEvent',
    'net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent': f'{PL}.entity.events.living.LivingKnockBackEvent',
    'net.neoforged.neoforge.event.entity.living.LivingDamageEvent': f'{PL}.entity.events.living.LivingDamageEvent',
    'net.neoforged.neoforge.event.entity.EntityJoinLevelEvent': f'{PL}.entity.events.EntityJoinLevelEvent',
    'net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent': f'{PL}.entity.events.EntityLeaveLevelEvent',
    'net.neoforged.neoforge.event.entity.EntityMountEvent': f'{PL}.entity.events.EntityMountEvent',
    'net.neoforged.neoforge.event.entity.EntityTeleportEvent': f'{PL}.entity.events.EntityTeleportEvent',
}

count = 0
for root, dirs, files in os.walk(ROOT):
    for fn in files:
        if not fn.endswith('.java'):
            continue
        p = os.path.join(root, fn)
        s = open(p, encoding='utf8').read()
        orig = s
        for old, new in MAP.items():
            s = s.replace(f'import {old};', f'import {new};')
            s = s.replace(f'import static {old};', f'import static {new};')
        if s != orig:
            open(p, 'w', encoding='utf8', newline='').write(s)
            count += 1
print('rewrote', count, 'files')
