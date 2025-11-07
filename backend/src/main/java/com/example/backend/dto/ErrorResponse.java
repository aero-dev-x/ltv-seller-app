package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class ErrorResponse {
	private int status;
	private String error;
	private String message;
	private LocalDateTime timestamp;
	private String path;

	public ErrorResponse() {
		this.timestamp = LocalDateTime.now();
	}

	public ErrorResponse(int status, String error, String message) {
		this.status = status;
		this.error = error != null ? error : "Error";
		this.message = message != null ? message : "An error occurred";
		this.timestamp = LocalDateTime.now();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error != null ? error : "Error";
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message != null ? message : "An error occurred";
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp != null ? timestamp : LocalDateTime.now();
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ErrorResponse that = (ErrorResponse) o;
		return status == that.status &&
				Objects.equals(error, that.error) &&
				Objects.equals(message, that.message) &&
				Objects.equals(timestamp, that.timestamp) &&
				Objects.equals(path, that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, error, message, timestamp, path);
	}

	@Override
	public String toString() {
		return "ErrorResponse{" +
				"status=" + status +
				", error='" + error + '\'' +
				", message='" + message + '\'' +
				", timestamp=" + timestamp +
				", path='" + path + '\'' +
				'}';
	}
}
