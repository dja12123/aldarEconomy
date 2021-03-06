package kr.dja.aldarEconomy.entity.player;

import java.util.UUID;

import kr.dja.aldarEconomy.entity.EconomyMap;
import kr.dja.aldarEconomy.entity.IEconomyObserver;

public class PlayerEconomyStorage extends EconomyMap<UUID, PlayerWallet>
{

	public PlayerEconomyStorage(IEconomyObserver<UUID, PlayerWallet> callback)
	{
		super(callback);
	}

	public void increaseEconomy(UUID key, int amount)
	{
		PlayerWallet wallet = this.eMap.get(key);
		if(wallet == null)
		{
			wallet = new PlayerWallet(key);
		}
		this.increaseEconomy(wallet, amount);
	}
}
