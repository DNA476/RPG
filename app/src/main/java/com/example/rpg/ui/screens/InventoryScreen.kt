package com.example.rpg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryItem
import com.example.rpg.data.inventory.ItemRarity
import com.example.rpg.ui.components.EquipmentItemIcon
import com.example.rpg.ui.components.InventoryItemIcon
import com.example.rpg.ui.localization.equipmentSlotResource
import com.example.rpg.ui.localization.inventoryItemNameResource
import com.example.rpg.ui.localization.itemBonusResource
import com.example.rpg.ui.localization.itemRarityResource
import com.example.rpg.ui.viewmodel.InventoryUiState
import kotlinx.coroutines.launch

private enum class InventoryFilter {
    ALL,
    EQUIPMENT,
    ARTIFACTS,
}

@Composable
fun InventoryScreen(
    inventory: InventoryUiState,
    onEquip: (String) -> Unit,
    onUnequip: (EquipmentSlot) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var filter by remember { mutableStateOf(InventoryFilter.ALL) }
    var slotFilter by remember { mutableStateOf<EquipmentSlot?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17352B), Color(0xFF0E1714), Color(0xFF080A09)),
                ),
            )
            .statusBarsPadding(),
    ) {
        InventoryHeader(
            currentPage = pagerState.currentPage,
            onBack = onBack,
            onPageSelected = { page ->
                scope.launch { pagerState.animateScrollToPage(page) }
            },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            if (page == 0) {
                InventoryPage(
                    inventory = inventory,
                    filter = filter,
                    slotFilter = slotFilter,
                    onFilterChanged = {
                        filter = it
                        slotFilter = null
                    },
                    onClearSlotFilter = { slotFilter = null },
                    onItemSelected = { selectedItem = it },
                    onShowEquipment = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    },
                )
            } else {
                EquipmentPage(
                    inventory = inventory,
                    onSlotSelected = { slot ->
                        slotFilter = slot
                        filter = if (slot == EquipmentSlot.ARTIFACT) {
                            InventoryFilter.ARTIFACTS
                        } else {
                            InventoryFilter.EQUIPMENT
                        }
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    onShowInventory = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                )
            }
        }
    }

    selectedItem?.let { item ->
        ItemDetailsDialog(
            item = item,
            equipped = inventory.equippedItemIds[item.slot] == item.id,
            onDismiss = { selectedItem = null },
            onEquip = {
                onEquip(item.id)
                selectedItem = null
            },
            onUnequip = {
                onUnequip(item.slot)
                selectedItem = null
            },
        )
    }
}

@Composable
private fun InventoryHeader(
    currentPage: Int,
    onBack: () -> Unit,
    onPageSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = stringResource(
                    if (currentPage == 0) R.string.inventory else R.string.equipment,
                ),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilterChip(
                selected = currentPage == 0,
                onClick = { onPageSelected(0) },
                label = { Text(stringResource(R.string.inventory)) },
                modifier = Modifier.weight(1f),
            )
            FilterChip(
                selected = currentPage == 1,
                onClick = { onPageSelected(1) },
                label = { Text(stringResource(R.string.equipment)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InventoryPage(
    inventory: InventoryUiState,
    filter: InventoryFilter,
    slotFilter: EquipmentSlot?,
    onFilterChanged: (InventoryFilter) -> Unit,
    onClearSlotFilter: () -> Unit,
    onItemSelected: (InventoryItem) -> Unit,
    onShowEquipment: () -> Unit,
) {
    val visibleItems = inventory.items.filter { item ->
        when {
            slotFilter != null -> item.slot == slotFilter
            filter == InventoryFilter.ARTIFACTS -> item.slot == EquipmentSlot.ARTIFACT
            filter == InventoryFilter.EQUIPMENT -> item.slot != EquipmentSlot.ARTIFACT
            else -> true
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.inventory_subtitle),
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Text(
            text = stringResource(
                R.string.inventory_progress,
                inventory.items.size,
                inventory.totalItemCount,
            ),
            color = Color(0xFFFFD166),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InventoryFilter.entries.forEach { option ->
                FilterChip(
                    selected = slotFilter == null && filter == option,
                    onClick = { onFilterChanged(option) },
                    label = {
                        Text(
                            stringResource(
                                when (option) {
                                    InventoryFilter.ALL -> R.string.inventory_all
                                    InventoryFilter.EQUIPMENT -> R.string.inventory_equipment
                                    InventoryFilter.ARTIFACTS -> R.string.inventory_artifacts
                                },
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        if (slotFilter != null) {
            Surface(
                color = Color(0xFFFFD166).copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            R.string.slot_filter,
                            stringResource(equipmentSlotResource(slotFilter)),
                        ),
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onClearSlotFilter) {
                        Text(stringResource(R.string.show_all))
                    }
                }
            }
        }
        if (visibleItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_items_for_filter),
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp),
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(visibleItems, key = InventoryItem::id) { item ->
                    InventoryItemCard(
                        item = item,
                        equipped = inventory.equippedItemIds[item.slot] == item.id,
                        onClick = { onItemSelected(item) },
                    )
                }
            }
        }
        Button(
            onClick = onShowEquipment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD166),
                contentColor = Color(0xFF111111),
            ),
        ) {
            Text(stringResource(R.string.view_equipment))
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    equipped: Boolean,
    onClick: () -> Unit,
) {
    val rarityColor = rarityColor(item.rarity)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (equipped) 2.dp else 1.dp,
            color = if (equipped) Color(0xFFFFD166) else rarityColor.copy(alpha = 0.7f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(rarityColor.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                InventoryItemIcon(
                    item = item,
                    modifier = Modifier.size(44.dp),
                    color = rarityColor,
                )
            }
            Text(
                text = stringResource(inventoryItemNameResource(item)),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(itemRarityResource(item.rarity)),
                color = rarityColor,
                style = MaterialTheme.typography.labelLarge,
            )
            if (equipped) {
                Text(
                    text = stringResource(R.string.equipped),
                    color = Color(0xFFFFD166),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun EquipmentPage(
    inventory: InventoryUiState,
    onSlotSelected: (EquipmentSlot) -> Unit,
    onShowInventory: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.equipment_subtitle),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            LoadoutBoard(
                inventory = inventory,
                onSlotSelected = onSlotSelected,
            )
        }
        item {
            EquipmentBonusSummary(inventory)
        }
        item {
            Text(
                text = stringResource(R.string.swipe_inventory_hint),
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedButton(
                onClick = onShowInventory,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.view_inventory))
            }
        }
    }
}

@Composable
private fun LoadoutBoard(
    inventory: InventoryUiState,
    onSlotSelected: (EquipmentSlot) -> Unit,
) {
    val itemsById = inventory.items.associateBy(InventoryItem::id)
    Surface(
        color = Color.Black.copy(alpha = 0.28f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(590.dp)
                .padding(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.vitruvian_loadout),
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .height(500.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit,
                alpha = 0.9f,
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.HEAD,
                item = inventory.equippedItemIds[EquipmentSlot.HEAD]?.let(itemsById::get),
                modifier = Modifier.align(Alignment.TopCenter),
                onClick = { onSlotSelected(EquipmentSlot.HEAD) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.CHEST,
                item = inventory.equippedItemIds[EquipmentSlot.CHEST]?.let(itemsById::get),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = (-115).dp),
                onClick = { onSlotSelected(EquipmentSlot.CHEST) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.HANDS,
                item = inventory.equippedItemIds[EquipmentSlot.HANDS]?.let(itemsById::get),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = (-115).dp),
                onClick = { onSlotSelected(EquipmentSlot.HANDS) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.WEAPON,
                item = inventory.equippedItemIds[EquipmentSlot.WEAPON]?.let(itemsById::get),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = 45.dp),
                onClick = { onSlotSelected(EquipmentSlot.WEAPON) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.LEGS,
                item = inventory.equippedItemIds[EquipmentSlot.LEGS]?.let(itemsById::get),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(y = 45.dp),
                onClick = { onSlotSelected(EquipmentSlot.LEGS) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.FEET,
                item = inventory.equippedItemIds[EquipmentSlot.FEET]?.let(itemsById::get),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(y = (-92).dp),
                onClick = { onSlotSelected(EquipmentSlot.FEET) },
            )
            EquipmentSlotCard(
                slot = EquipmentSlot.ARTIFACT,
                item = inventory.equippedItemIds[EquipmentSlot.ARTIFACT]?.let(itemsById::get),
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { onSlotSelected(EquipmentSlot.ARTIFACT) },
            )
        }
    }
}

@Composable
private fun EquipmentSlotCard(
    slot: EquipmentSlot,
    item: InventoryItem?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val accent = item?.let { rarityColor(it.rarity) } ?: Color.White.copy(alpha = 0.55f)
    Surface(
        modifier = modifier
            .width(116.dp)
            .clickable(onClick = onClick),
        color = Color(0xFF1B2925).copy(alpha = 0.96f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (item == null) {
                EquipmentItemIcon(
                    slot = slot,
                    modifier = Modifier.size(28.dp),
                    color = accent,
                )
            } else {
                InventoryItemIcon(
                    item = item,
                    modifier = Modifier.size(28.dp),
                    color = accent,
                )
            }
            Text(
                text = stringResource(equipmentSlotResource(slot)),
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
            Text(
                text = item?.let {
                    stringResource(inventoryItemNameResource(it))
                } ?: stringResource(R.string.empty_slot),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EquipmentBonusSummary(inventory: InventoryUiState) {
    val itemsById = inventory.items.associateBy(InventoryItem::id)
    val equippedItems = inventory.equippedItemIds.values.mapNotNull(itemsById::get)
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.loadout_bonuses),
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            if (equippedItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_equipment_bonuses),
                    color = Color.White.copy(alpha = 0.7f),
                )
            } else {
                equippedItems.flatMap(InventoryItem::bonuses)
                    .groupBy { it.type }
                    .mapValues { (_, bonuses) -> bonuses.sumOf { it.value } }
                    .forEach { (type, value) ->
                        Text(
                            text = stringResource(itemBonusResource(type), value),
                            color = Color.White,
                        )
                    }
            }
            Text(
                text = stringResource(R.string.bonuses_active_note),
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ItemDetailsDialog(
    item: InventoryItem,
    equipped: Boolean,
    onDismiss: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit,
) {
    val rarityColor = rarityColor(item.rarity)
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            InventoryItemIcon(
                item = item,
                modifier = Modifier.size(58.dp),
                color = rarityColor,
            )
        },
        title = {
            Text(
                text = stringResource(inventoryItemNameResource(item)),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(equipmentSlotResource(item.slot)),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(itemRarityResource(item.rarity)),
                    color = rarityColor,
                    fontWeight = FontWeight.Bold,
                )
                item.bonuses.forEach { bonus ->
                    Text(text = stringResource(itemBonusResource(bonus.type), bonus.value))
                }
                Text(
                    text = stringResource(R.string.bonuses_active_note),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
        confirmButton = {
            Button(onClick = if (equipped) onUnequip else onEquip) {
                Text(stringResource(if (equipped) R.string.unequip else R.string.equip))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
    )
}

private fun rarityColor(rarity: ItemRarity): Color = when (rarity) {
    ItemRarity.COMMON -> Color(0xFFE5E7EB)
    ItemRarity.RARE -> Color(0xFF60A5FA)
    ItemRarity.EPIC -> Color(0xFFC084FC)
    ItemRarity.LEGENDARY -> Color(0xFFFFD166)
}
