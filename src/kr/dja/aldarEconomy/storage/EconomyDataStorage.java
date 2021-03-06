package kr.dja.aldarEconomy.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import kr.dja.aldarEconomy.IntLocation;
import kr.dja.aldarEconomy.bank.TradeTracker;
import kr.dja.aldarEconomy.coininfo.CoinInfo;
import kr.dja.aldarEconomy.entity.DependType;
import kr.dja.aldarEconomy.entity.chest.ChestEconomyChild;
import kr.dja.aldarEconomy.entity.chest.ChestEconomyStorage;
import kr.dja.aldarEconomy.entity.chest.ChestWallet;
import kr.dja.aldarEconomy.entity.chest.IChestObserver;
import kr.dja.aldarEconomy.entity.enderChest.EnderChestEconomyStorage;
import kr.dja.aldarEconomy.entity.enderChest.EnderChestWallet;
import kr.dja.aldarEconomy.entity.itemEntity.ItemEconomyChild;
import kr.dja.aldarEconomy.entity.itemEntity.ItemEconomyStorage;
import kr.dja.aldarEconomy.entity.itemEntity.ItemWallet;
import kr.dja.aldarEconomy.entity.player.PlayerEconomyStorage;
import kr.dja.aldarEconomy.entity.player.PlayerWallet;

public class EconomyDataStorage
{// dao
	private final CoinInfo moneyInfo;
	private final Logger logger;
	public final String dataGroupName;
	
	private final Map<UUID, PlayerEconomy> _playerEconomyMap;
	public final Map<UUID, PlayerEconomy> playerEconomyMap;
	
	public final ChestEconomyStorage chestDependEconomy;
	public final PlayerEconomyStorage playerDependEconomy;
	public final EnderChestEconomyStorage playerEnderChestEconomy;
	public final ItemEconomyStorage itemEconomyStorage;
	
	public EconomyDataStorage(CoinInfo moneyInfo, Logger logger, String dataGroupName)
	{
		this.moneyInfo = moneyInfo;
		this.logger = logger;
		this.dataGroupName = dataGroupName;
		
		this._playerEconomyMap = new HashMap<>();
		this.playerEconomyMap = Collections.unmodifiableMap(this._playerEconomyMap);
		this.chestDependEconomy = new ChestEconomyStorage(new ChestCallback());
		this.playerDependEconomy = new PlayerEconomyStorage(this::onModifyPlayerMoney);
		this.playerEnderChestEconomy = new EnderChestEconomyStorage(this::onModifyEnderChestMoney);
		this.itemEconomyStorage = new ItemEconomyStorage(this::onModifyItemMoney);
	}
	
	public MoneyDetailResult getMoneyDetail(UUID player)
	{
		long playerTotal = this.getPlayerMoneyTotal(player);
		int playerInvMoney = this.playerDependEconomy.getMoney(player);
		int chestMoney = 0;
		int itemMoney = 0;
		Map<IntLocation, Integer> chestMoneyMap = new HashMap<>();
		for(ChestEconomyChild child : this.chestDependEconomy.childSet)
		{
			ChestWallet w = child.eMap.get(player);
			if(w == null) continue;
			IntLocation intLoc = child.getLocation();
			chestMoneyMap.put(intLoc, w.getMoney());
			chestMoney += w.getMoney();
		}
		Map<IntLocation, Integer> itemMoneyMap = new HashMap<>();
		for(ItemEconomyChild child : this.itemEconomyStorage.eMap.values())
		{
			ItemWallet w = child.eMap.get(player);
			if(w == null) continue;
			Entity entity = Bukkit.getEntity(child.parent);
			IntLocation intLoc = new IntLocation(entity.getLocation());
			int mapMoney = itemMoneyMap.getOrDefault(intLoc, 0);
			itemMoneyMap.put(intLoc, mapMoney + w.getMoney());
			itemMoney += w.getMoney();
		}
		int enderChestMoney = this.playerEnderChestEconomy.getMoney(player);
		return new MoneyDetailResult(player, playerTotal, chestMoney, chestMoneyMap, playerInvMoney, enderChestMoney, itemMoney, itemMoneyMap);
	}
	
	public long getPlayerMoneyTotal(UUID player)
	{
		Player p = Bukkit.getPlayer(player);
		if(p == null)
		{
			this.logger.log(Level.WARNING, String.format("존재하지 않는 플레이어 (%s)",player));
			return 0;
		}
		PlayerEconomy e = this._playerEconomyMap.get(player);
		if(e == null) return 0;
		return e.getMoney();
	}
	
	private class ChestCallback implements IChestObserver
	{
		@Override
		public void modifyMoney(UUID uuid, UUID player, ChestWallet wallet, int amount)
		{
			if(wallet.ownerType == DependType.PLAYER)
			{
				EconomyDataStorage.this.modifyPlayerMoney(player, amount);
				
			}
		}

		@Override
		public void appendKey(IntLocation loc, ChestEconomyChild obj)
		{
			//Bukkit.getServer().broadcastMessage(String.format("appendKey %s", loc));
		}

		@Override
		public void deleteKey(IntLocation loc, ChestEconomyChild obj)
		{
			//Bukkit.getServer().broadcastMessage(String.format("deleteKey %s", loc));
		}
	}

	
	private void onModifyPlayerMoney(PlayerWallet wallet, int amount)
	{
		this.modifyPlayerMoney(wallet.depend, amount);
	}
	
	private void onModifyEnderChestMoney(EnderChestWallet wallet, int amount)
	{
		
		this.modifyPlayerMoney(wallet.depend, amount);
	}
	
	private void onModifyItemMoney(ItemWallet wallet, int amount)
	{
		if(wallet.ownerType == DependType.PLAYER)
		{
			this.modifyPlayerMoney(wallet.depend, amount);
		}
	}

	private void modifyPlayerMoney(UUID player, int amount)
	{
		PlayerEconomy playerEconomy = this._playerEconomyMap.get(player);
		if(playerEconomy == null)
		{
			playerEconomy = new PlayerEconomy(player);
			this._playerEconomyMap.put(player, playerEconomy);
		}
		playerEconomy.money += amount;
		if(playerEconomy.money <= 0)
		{
			this._playerEconomyMap.remove(player);
		}
		//Bukkit.getServer().broadcastMessage(String.format("modifyEconomy %s %s", Bukkit.getPlayer(player).getName(), amount));
	}
}