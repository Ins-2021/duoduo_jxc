class BluetoothPrinter {
  private deviceId: string | null = null
  private serviceId: string | null = null
  private characteristicId: string | null = null
  private connected = false

  async init(): Promise<void> {
    try {
      await uni.openBluetoothAdapter()
      console.log('蓝牙初始化成功')
    } catch {
      throw new Error('请开启蓝牙')
    }
  }

  async connect(deviceId: string): Promise<boolean> {
    try {
      await uni.createBLEConnection({ deviceId })
      this.deviceId = deviceId
      const services: any = await uni.getBLEDeviceServices({ deviceId })
      this.serviceId = services.services[0]?.uuid
      if (!this.serviceId) return false
      const chars: any = await uni.getBLEDeviceCharacteristics({ deviceId, serviceId: this.serviceId })
      this.characteristicId = chars.characteristics[0]?.uuid
      if (!this.characteristicId) return false
      this.connected = true
      return true
    } catch (e) {
      console.error('蓝牙连接失败:', e)
      return false
    }
  }

  async printBundleLabel(bundle: any): Promise<void> {
    if (!this.connected || !this.deviceId || !this.serviceId || !this.characteristicId) {
      throw new Error('打印机未连接')
    }
    const cmd = [
      `SIZE 50 mm, 30 mm`,
      `GAP 2 mm, 0 mm`,
      `DIRECTION 1`,
      `CLS`,
      `TEXT 10,10,"2",0,1,1,"${bundle.bundleNo}"`,
      `TEXT 10,40,"1",0,1,1,"款号:${bundle.styleCode}"`,
      `TEXT 10,65,"1",0,1,1,"${bundle.color}/${bundle.size}"`,
      `TEXT 10,90,"1",0,1,1,"数量:${bundle.quantity}"`,
      `QRCODE 150,50,H,3,A,0,"${bundle.bundleNo}"`,
      `PRINT 1`
    ].join('\n')
    const buffer = new ArrayBuffer(cmd.length)
    const dataView = new DataView(buffer)
    for (let i = 0; i < cmd.length; i++) {
      dataView.setUint8(i, cmd.charCodeAt(i))
    }
    await uni.writeBLECharacteristicValue({
      deviceId: this.deviceId,
      serviceId: this.serviceId,
      characteristicId: this.characteristicId,
      value: buffer
    })
  }

  disconnect(): void {
    if (this.deviceId) {
      uni.closeBLEConnection({ deviceId: this.deviceId })
      this.connected = false
      this.deviceId = null
    }
  }
}

export const bluetoothPrinter = new BluetoothPrinter()
