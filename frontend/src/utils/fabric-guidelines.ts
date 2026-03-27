import { Canvas } from 'fabric'

export const initAligningGuidelines = (canvas: Canvas) => {
  const ctx = canvas.getSelectionContext()
  const aligningLineOffset = 5
  const aligningLineMargin = 4
  const aligningLineWidth = 1
  const aligningLineColor = 'rgba(255,0,0,0.8)'
  const verticalLines: any[] = []
  const horizontalLines: any[] = []

  const drawVerticalLine = (coords: any) => {
    drawLine(
      coords.x + 0.5,
      coords.y1 > coords.y2 ? coords.y2 : coords.y1,
      coords.x + 0.5,
      coords.y2 > coords.y1 ? coords.y2 : coords.y1
    )
  }

  const drawHorizontalLine = (coords: any) => {
    drawLine(
      coords.x1 > coords.x2 ? coords.x2 : coords.x1,
      coords.y + 0.5,
      coords.x2 > coords.x1 ? coords.x2 : coords.x1,
      coords.y + 0.5
    )
  }

  const drawLine = (x1: number, y1: number, x2: number, y2: number) => {
    const vt = canvas.viewportTransform || [1, 0, 0, 1, 0, 0]
    ctx.save()
    ctx.lineWidth = aligningLineWidth
    ctx.strokeStyle = aligningLineColor
    ctx.beginPath()
    ctx.moveTo(x1 * vt[0] + vt[4], y1 * vt[3] + vt[5])
    ctx.lineTo(x2 * vt[0] + vt[4], y2 * vt[3] + vt[5])
    ctx.stroke()
    ctx.restore()
  }

  const isInRange = (value1: number, value2: number) => {
    return Math.abs(Math.round(value1) - Math.round(value2)) <= aligningLineMargin / (canvas.getZoom() || 1)
  }

  canvas.on('object:moving', (e: any) => {
    const activeObject = e.target
    if (!activeObject) return

    const canvasObjects = canvas.getObjects()
    const activeObjectCenter = activeObject.getCenterPoint()
    const activeObjectLeft = activeObjectCenter.x
    const activeObjectTop = activeObjectCenter.y
    const activeObjectBoundingRect = activeObject.getBoundingRect()
    const activeObjectHeight = activeObjectBoundingRect.height / (canvas.getZoom() || 1)
    const activeObjectWidth = activeObjectBoundingRect.width / (canvas.getZoom() || 1)
    let horizontalInTheRange = false
    let verticalInTheRange = false

    verticalLines.length = 0
    horizontalLines.length = 0

    for (let i = canvasObjects.length; i--; ) {
      if (canvasObjects[i] === activeObject) continue
      const objectBoundingRect = canvasObjects[i].getBoundingRect()
      const objectHeight = objectBoundingRect.height / (canvas.getZoom() || 1)
      const objectWidth = objectBoundingRect.width / (canvas.getZoom() || 1)
      const objectCenter = canvasObjects[i].getCenterPoint()
      const objectLeft = objectCenter.x
      const objectTop = objectCenter.y

      // snap to left
      if (isInRange(activeObjectLeft - activeObjectWidth / 2, objectLeft - objectWidth / 2)) {
        verticalInTheRange = true
        activeObject.setPositionByOrigin(
          { x: objectLeft - objectWidth / 2 + activeObjectWidth / 2, y: activeObjectTop },
          'center',
          'center'
        )
        verticalLines.push({
          x: objectLeft - objectWidth / 2,
          y1:
            objectTop < activeObjectTop
              ? objectTop - objectHeight / 2 - aligningLineOffset
              : objectTop + objectHeight / 2 + aligningLineOffset,
          y2:
            activeObjectTop > objectTop
              ? activeObjectTop + activeObjectHeight / 2 + aligningLineOffset
              : activeObjectTop - activeObjectHeight / 2 - aligningLineOffset
        })
      }

      // snap to center-x
      if (isInRange(activeObjectLeft, objectLeft)) {
        verticalInTheRange = true
        activeObject.setPositionByOrigin({ x: objectLeft, y: activeObjectTop }, 'center', 'center')
        verticalLines.push({
          x: objectLeft,
          y1:
            objectTop < activeObjectTop
              ? objectTop - objectHeight / 2 - aligningLineOffset
              : objectTop + objectHeight / 2 + aligningLineOffset,
          y2:
            activeObjectTop > objectTop
              ? activeObjectTop + activeObjectHeight / 2 + aligningLineOffset
              : activeObjectTop - activeObjectHeight / 2 - aligningLineOffset
        })
      }

      // snap to top
      if (isInRange(activeObjectTop - activeObjectHeight / 2, objectTop - objectHeight / 2)) {
        horizontalInTheRange = true
        activeObject.setPositionByOrigin(
          { x: activeObjectLeft, y: objectTop - objectHeight / 2 + activeObjectHeight / 2 },
          'center',
          'center'
        )
        horizontalLines.push({
          y: objectTop - objectHeight / 2,
          x1:
            objectLeft < activeObjectLeft
              ? objectLeft - objectWidth / 2 - aligningLineOffset
              : objectLeft + objectWidth / 2 + aligningLineOffset,
          x2:
            activeObjectLeft > objectLeft
              ? activeObjectLeft + activeObjectWidth / 2 + aligningLineOffset
              : activeObjectLeft - activeObjectWidth / 2 - aligningLineOffset
        })
      }

      // snap to center-y
      if (isInRange(activeObjectTop, objectTop)) {
        horizontalInTheRange = true
        activeObject.setPositionByOrigin({ x: activeObjectLeft, y: objectTop }, 'center', 'center')
        horizontalLines.push({
          y: objectTop,
          x1:
            objectLeft < activeObjectLeft
              ? objectLeft - objectWidth / 2 - aligningLineOffset
              : objectLeft + objectWidth / 2 + aligningLineOffset,
          x2:
            activeObjectLeft > objectLeft
              ? activeObjectLeft + activeObjectWidth / 2 + aligningLineOffset
              : activeObjectLeft - activeObjectWidth / 2 - aligningLineOffset
        })
      }
    }
  })

  canvas.on('before:render', () => {
    canvas.clearContext(canvas.getSelectionContext())
  })

  canvas.on('after:render', () => {
    for (let i = verticalLines.length; i--; ) {
      drawVerticalLine(verticalLines[i])
    }
    for (let i = horizontalLines.length; i--; ) {
      drawHorizontalLine(horizontalLines[i])
    }
    verticalLines.length = horizontalLines.length = 0
  })

  canvas.on('mouse:up', () => {
    verticalLines.length = horizontalLines.length = 0
    canvas.requestRenderAll()
  })
}