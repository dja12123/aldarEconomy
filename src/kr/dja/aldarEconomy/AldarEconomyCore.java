package kr.dja.aldarEconomy;


import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.ServiceManager;

import kr.dja.aldarEconomy.api.AldarEconomy;
import kr.dja.aldarEconomy.api.AldarEconomyProvider;
import kr.dja.aldarEconomy.api.VaultEconomyProvider;
import kr.dja.aldarEconomy.api.token.APITokenManager;
import kr.dja.aldarEconomy.bank.Bank;
import kr.dja.aldarEconomy.bank.TradeTracker;
import kr.dja.aldarEconomy.coininfo.CoinInfo;
import kr.dja.aldarEconomy.command.CommandManager;
import kr.dja.aldarEconomy.eventListener.EventListener;
import kr.dja.aldarEconomy.setting.ConfigLoader;
import kr.dja.aldarEconomy.storage.EconomyDataStorage;
import kr.dja.aldarEconomy.tracker.chest.ChestTracker;
import kr.dja.aldarEconomy.tracker.item.ItemTracker;
import net.milkbowl.vault.economy.Economy;

public class AldarEconomyCore extends JavaPlugin
{
	public static final String PLUGIN_NAME = "aldarEconomy";
	
	private Logger logger;
	private String version;
	private APITokenManager apiTokenManager;
	private PluginManager pluginManager;
	private ConfigLoader configLoader;
	private CoinInfo coinInfo;
	private EconomyUtil util;
	private EconomyDataStorage storage;
	private EventListener eventListener;
	private TradeTracker tradeTracker;
	private ChestTracker chestTracker;
	private ItemTracker itemTracker;
	private Bank bank;
	
	private CommandManager commandManager;
	private AldarEconomyProvider provider;
	private VaultEconomyProvider vaultProvider;
	
	
	@Override
	public void onEnable()
	{
		this.version = this.getDescription().getVersion();
		
		this.logger = this.getLogger();
		this.configLoader = new ConfigLoader(this, this.logger);
		this.coinInfo = new CoinInfo();
		this.apiTokenManager = new APITokenManager();
		
		this.configLoader.registerModule("coinInfo", this.coinInfo);
		this.configLoader.registerModule("apiTokens", this.apiTokenManager);
		
		this.configLoader.loadConfig();
		
		this.logger.info("\n"+this.configLoader.toString());

		
		this.util = new EconomyUtil(this.coinInfo);
		
		this.storage = new EconomyDataStorage(this.coinInfo, this.logger, "aldarDefault");
		this.tradeTracker = new TradeTracker(this.logger, this.apiTokenManager);
		this.itemTracker = new ItemTracker(this, this.util, this.storage.itemEconomyStorage, this.storage.playerDependEconomy, this.tradeTracker, this.logger);
		this.chestTracker = new ChestTracker(this.itemTracker, this.util, this.storage.chestDependEconomy, this.storage.playerDependEconomy, this.storage.playerEnderChestEconomy, this.tradeTracker, this.logger);
		this.eventListener = new EventListener(this.util, this.chestTracker, this.itemTracker, this.logger);
		this.bank = new Bank(this.coinInfo, this.util, this.storage, this.chestTracker, this.tradeTracker);
		
		this.pluginManager = this.getServer().getPluginManager();
		this.pluginManager.registerEvents(this.eventListener, this);
		
		this.provider = new AldarEconomyProvider(this, this.apiTokenManager, this.storage, this.bank, this.util);
		this.commandManager = new CommandManager(this, this.storage, this.provider);
		this.vaultProvider = new VaultEconomyProvider(this.apiTokenManager, this, this.provider, this.logger);
		
		this.vaultProvider.setState(true);
		
		ServicesManager sm = this.getServer().getServicesManager();
		sm.register(Economy.class, this.vaultProvider, this, ServicePriority.High);
		sm.register(AldarEconomy.class, this.provider, this, ServicePriority.Normal);
		
		this.logger.info(PLUGIN_NAME+version+" enabled by camelCase");
	}
	
	@Override
	public void onDisable()
	{
		ServicesManager sm = this.getServer().getServicesManager();
		sm.unregisterAll(this);
		this.vaultProvider.setState(false);
		this.configLoader.saveConfig();
		this.logger.info(PLUGIN_NAME+version+" disabled by camelCase");
	}
	
	public AldarEconomy getEconomyModule()
	{
		return this.provider;
	}

}