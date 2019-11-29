package kr.dja.aldarEconomy.dataObject.container.chest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import kr.dja.aldarEconomy.dataObject.Wallet;
import kr.dja.aldarEconomy.dataObject.container.IntLocation;


public class ChestEconomyChild
{
	private final IChestObserver callback;
	private final Map<UUID, ChestWallet> _eMap;
	public final Map<UUID, ChestWallet> eMap;
	private int totalMoney;
	
	public final UUID uuid;
	final Set<IntLocation> parents;

	ChestEconomyChild(IChestObserver callback)
	{
		this.callback = callback;
		this._eMap = new HashMap<>();
		this.eMap = Collections.unmodifiableMap(this._eMap);
		this.totalMoney = 0;
		this.uuid = UUID.randomUUID();
		this.parents = new HashSet<>();
	}

	public void increaseEconomy(UUID key, int amount)
	{
		this.totalMoney += amount;
		if(this._eMap.containsKey(key))
		{
			ChestWallet wallet = this._eMap.get(key);
			
			wallet.setMoney(wallet.getMoney() + amount);
			this.callback.increaseEconomy(this.uuid, key, wallet, false);
		}
		else
		{
			ChestWallet wallet = new ChestWallet(key, amount);
			this._eMap.put(key, wallet);
			this.callback.increaseEconomy(this.uuid, key, wallet, true);
		}
	}
	
	public void decreaseEconomy(UUID key, int amount)
	{
		ChestWallet wallet = this._eMap.get(key);
		wallet.setMoney(wallet.getMoney() - amount);
		this.totalMoney -= amount;
		if(wallet.getMoney() == 0)
		{
			this._eMap.remove(key);
			this.callback.decreaseEconomy(this.uuid, key, wallet, true);
		}
		else
		{
			this.callback.decreaseEconomy(this.uuid, key, wallet, false);
		}
	}
	
	public int getMoney(UUID key)
	{
		Wallet wallet = this._eMap.get(key);
		if(wallet == null) return 0;
		return wallet.getMoney();
	}

	public int getTotalMoney()
	{
		return this.totalMoney;
	}
}