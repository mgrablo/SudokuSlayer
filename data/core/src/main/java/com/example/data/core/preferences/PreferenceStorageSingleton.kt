package com.example.data.core.preferences

object PreferenceStorageSingleton {
	private var factory: PreferenceStorageFactory? = null
	private var instances: MutableMap<String, PreferenceStorage> = mutableMapOf()

	@Synchronized
	fun initialize(factory: PreferenceStorageFactory) {
		if (this.factory == null) {
			this.factory = factory
		}
	}

	@Synchronized
	fun getInstance(name: String = "default"): PreferenceStorage = instances.getOrPut(name) {
		checkNotNull(factory) {
			"PreferenceStorageFactory is not initialized"
		}
		factory!!.create(name)
	}

	@Synchronized
	fun reset() {
		instances.clear()
		factory = null
	}
}
