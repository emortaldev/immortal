package dev.emortal.immortal.utils;

import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

public record Quaternion(double x, double y, double z, double w) {

    public Quaternion(@NotNull Quaternion q) {
		this(q.x, q.y, q.z, q.w);
	}

	public Quaternion(@NotNull Vec axis, double angle) {
		this(
				axis.x() * Math.sin(angle / 2),
				axis.y() * Math.sin(angle / 2),
				axis.z() * Math.sin(angle / 2),
				Math.cos(angle / 2)
		);
	}

	public double norm() {
		return Math.sqrt(this.dot(this));
	}

	public @NotNull Quaternion mul(@NotNull Quaternion q) {
		double nw = this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z;
		double nx = this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y;
		double ny = this.w * q.y + this.y * q.w + this.z * q.x - this.x * q.z;
		double nz = this.w * q.z + this.z * q.w + this.x * q.y - this.y * q.x;
		return new Quaternion(nx, ny, nz, nw);
	}

	public @NotNull Quaternion mul(double scale) {
		if (scale == 1) return this;
		return new Quaternion(this.x * scale, this.y * scale, this.z * scale, this.w * scale);
	}

	public @NotNull Quaternion divThis(double scale) {
		if (scale == 1) return this;
		return new Quaternion(this.x / scale, this.y / scale, this.z / scale, this.w / scale);
	}

	public double dot(@NotNull Quaternion q) {
		return this.x * q.x + this.y * q.y + this.z * q.z + this.w * q.w;
	}

	public boolean same(@NotNull Quaternion q) {
		return this.x == q.x && this.y == q.y && this.z == q.z && this.w == q.w;
	}

	public @NotNull Quaternion lerp(@NotNull Quaternion q, double t) {
		if (this.equals(q)) return this;

		double d = this.dot(q);
		double qx, qy, qz, qw;

		if (d < 0f) {
			qx = -q.x;
			qy = -q.y;
			qz = -q.z;
			qw = -q.w;
			d = -d;
		} else {
			qx = q.x;
			qy = q.y;
			qz = q.z;
			qw = q.w;
		}

		double f0, f1;

		if ((1 - d) > 0.1f) {
			double angle = Math.acos(d);
			double s = Math.sin(angle);
			double tAngle = t * angle;
			f0 = Math.sin(angle - tAngle) / s;
			f1 = Math.sin(tAngle) / s;
		} else {
			f0 = 1 - t;
			f1 = t;
		}

		return new Quaternion(
				f0 * this.x + f1 * qx,
				f0 * this.y + f1 * qy,
				f0 * this.z + f1 * qz,
				f0 * this.w + f1 * qw
		);
	}

	public @NotNull Quaternion normalize() {
		return this.divThis(this.norm());
	}

	/**
	 * Converts this Quaternion into a matrix, returning it as a float array.
	 */
	public float[] toMatrix() {
		float[] matrixs = new float[16];
		this.toMatrix(matrixs);
		return matrixs;
	}

	/**
	 * Converts this Quaternion into a matrix, placing the values into the given array.
	 * @param matrix 16-length float array.
	 */
	public void toMatrix(float[] matrix) {
		matrix[3] = 0.0f;
		matrix[7] = 0.0f;
		matrix[11] = 0.0f;
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;

		matrix[0] = (float) (1.0f - (2.0f * ((this.y * this.y) + (this.z * this.z))));
		matrix[1] = (float) (2.0f * ((x * y) - (z * w)));
		matrix[2] = (float) (2.0f * ((x * z) + (y * w)));
		
		matrix[4] = (float) (2.0f * ((x * y) + (z * w)));
		matrix[5] = (float) (1.0f - (2.0f * ((x * x) + (z * z))));
		matrix[6] = (float) (2.0f * ((y * z) - (x * w)));
		
		matrix[8] = (float) (2.0f * ((x * z) - (y * w)));
		matrix[9] = (float) (2.0f * ((y * z) + (x * w)));
		matrix[10] = (float) (1.0f - (2.0f * ((x * x) + (y * y))));
	}

	/**
	 * @return a float array of [x, y, z, w], which is compatible with display entity rotation
	 */
	public float[] toMinecraftFloat() {
		return new float[] { (float)this.x, (float)this.y, (float)this.z, (float)this.w };
	}

}