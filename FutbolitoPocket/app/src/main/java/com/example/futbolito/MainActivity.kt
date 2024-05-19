package com.example.futbolito

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Half.EPSILON
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futbolito.ui.theme.FutbolitoTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    private var sensorAcelerometro: Sensor? = null
    private lateinit var gyroscopeSensorHandler: GyroscopeSensorHandler
    private lateinit var acelerometroSensorHandler: AcelerometroSensorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorAcelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensorHandler = GyroscopeSensorHandler()
        acelerometroSensorHandler= AcelerometroSensorHandler()
        setContent {
            FutbolitoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FutbolitoPocket(sensor!=null)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensor?.let { sensor ->
            sensorManager.registerListener(gyroscopeSensorHandler, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorAcelerometro?.let { sensor ->
           sensorManager.registerListener(acelerometroSensorHandler, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(gyroscopeSensorHandler)
        sensorManager.unregisterListener(acelerometroSensorHandler)
    }
}

@Composable
fun FutbolitoPocket(
    giroscopio:Boolean,
    view: ViewModelTest= viewModel(factory = ViewModelTest.Factory)
) {
    SharedViewModel.viewModel = view
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cancha),
            contentDescription = "Descripción del fondo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.91f)
        )
        var maxWidth = constraints.maxWidth.toFloat()
        SharedViewModel.viewModel.maxwidth=maxWidth
        if(SharedViewModel.viewModel.mueve){
            verificarGol(maxWidth)
        }
        BoxWithConstraints(
            modifier = Modifier
                .size(width = 65.dp, height = 53.dp)
                .offset { IntOffset((maxWidth / 2 - 25.dp.toPx()).roundToInt(), 0) }
                .background(Color(244, 67, 54, 255)),
            content = {}
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight(.80f)
                .fillMaxWidth()){
            var maxHeight = constraints.maxHeight.toFloat() // Altura máxima del Box
            SharedViewModel.viewModel.maxheigth=maxHeight
            balonEnCentro()
            Image(
                painter = painterResource(id = R.drawable.balon),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .offset {
                        IntOffset(
                            SharedViewModel.viewModel.posicion.x.roundToInt().coerceIn(0, maxWidth.roundToInt() - 50.dp.toPx().roundToInt()),
                            SharedViewModel.viewModel.posicion.y.roundToInt().coerceIn(0, maxHeight.roundToInt() - 50.dp.toPx().roundToInt())
                        )
                    })
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.09f)
                .background(Color(3, 169, 244, 255), RectangleShape)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(49, 221, 224, 255))
            ) {
                Text(
                    text = "${SharedViewModel.viewModel.estadoBalon}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = SharedViewModel.viewModel.colorEstado
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FutbolitoTheme {
        FutbolitoPocket(true)
    }
}

object SharedViewModel {
    var viewModel: ViewModelTest = ViewModelTest()
}

class GyroscopeSensorHandler : SensorEventListener {
    // Create a constant to convert nanoseconds to seconds.
    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (timestamp != 0f && event != null) {
            val dT = (event.timestamp - timestamp) * NS2S
            // Axis of the rotation sample, not normalized yet.
            var axisX: Float = event.values[0]
            var axisY: Float = event.values[1]
            var axisZ: Float = event.values[2]

            SharedViewModel.viewModel.actualizarX(axisX)
            SharedViewModel.viewModel.actualizarY(axisY)
            SharedViewModel.viewModel.actualizarZ(axisZ)

            SharedViewModel.viewModel.posicion = Offset(
                SharedViewModel.viewModel.posicion.x + axisX*20f,
                SharedViewModel.viewModel.posicion.y + axisY*20f
            )
            // Calculate the angular speed of the sample
            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude
                axisY /= omegaMagnitude
                axisZ /= omegaMagnitude
            }
            val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
            val sinThetaOverTwo: Float = sin(thetaOverTwo)
            val cosThetaOverTwo: Float = cos(thetaOverTwo)
            deltaRotationVector[0] = sinThetaOverTwo * axisX
            deltaRotationVector[1] = sinThetaOverTwo * axisY
            deltaRotationVector[2] = sinThetaOverTwo * axisZ
            deltaRotationVector[3] = cosThetaOverTwo
        }
        timestamp = event?.timestamp?.toFloat() ?: 0f
        val deltaRotationMatrix = FloatArray(9) { 0f }
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
    }
}

class AcelerometroSensorHandler() : SensorEventListener {
    private var gravity = FloatArray(3) // Declaración de la variable gravity
    private var linear_acceleration = FloatArray(3) // Declaración de la variable linear_acceleration
    private val alpha: Float = 0.8f

    override fun onSensorChanged(event: SensorEvent?) {
        // Isolate the force of gravity with the low-pass filter.
        if (event != null) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0]
            linear_acceleration[1] = event.values[1] - gravity[1]
            linear_acceleration[2] = event.values[2] - gravity[2]

            SharedViewModel.viewModel.actualizarxAcelerometro(linear_acceleration[0])
            SharedViewModel.viewModel.actualizaryAcelerometro(linear_acceleration[1])
            SharedViewModel.viewModel.actualizarzAcelerometro(linear_acceleration[2])
            //se invierte el signo (-) para que cuando se incline a la derecha el balon se vaya a la derecha y no a la izquiereda
            var posX = (SharedViewModel.viewModel.posicion.x - linear_acceleration[0] * 20f)//.coerceIn(0f)
            var posY = (SharedViewModel.viewModel.posicion.y + linear_acceleration[1] * 36f)//.coerceIn(0f, screenHeight - imageHeight)

            // Actualizar la posición en el ViewModel
            SharedViewModel.viewModel.posicion = Offset(posX, posY)
            if(SharedViewModel.viewModel.mueve!=true){
                SharedViewModel.viewModel.mueve=true
            }
        }else{
            SharedViewModel.viewModel.mueve=false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}

@Composable
fun balonEnCentro(){
    SharedViewModel.viewModel.posicion= Offset(SharedViewModel.viewModel.maxwidth/2-50,SharedViewModel.viewModel.maxheigth)
}

@Composable
fun verificarGol(screenWidth: Float) {
    val imageSize = with(LocalDensity.current) { 50.dp.toPx() }

    // Coordenadas del centro superior de la pantalla
    val topCenterX  = screenWidth / 2
    val topCenterY = 0f // El centro superior siempre tendrá la coordenada Y como 0


    val imageCenterX = SharedViewModel.viewModel.posicion.x + imageSize / 2

    val imageCrossesTopCenter = imageCenterX-topCenterX in -25f..25f && SharedViewModel.viewModel.posicion.y-topCenterY in -800f..40f
    if (imageCrossesTopCenter) {
        SharedViewModel.viewModel.estadoBalon="Gol"
        SharedViewModel.viewModel.colorEstado = Color(255, 255, 255, 255)
    }else{
        SharedViewModel.viewModel.estadoBalon="Cancha"
        SharedViewModel.viewModel.colorEstado = Color(0, 0, 0, 255)
    }
}
